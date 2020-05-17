package com.gis.landsurveyor

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.speech.tts.TextToSpeech.OnInitListener
import android.text.format.DateUtils
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.esri.arcgisruntime.concurrent.ListenableFuture
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Basemap
import com.esri.arcgisruntime.mapping.Viewpoint
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.navigation.DestinationStatus
import com.esri.arcgisruntime.navigation.TrackingStatus
import com.esri.arcgisruntime.symbology.SimpleLineSymbol
import com.esri.arcgisruntime.tasks.networkanalysis.RouteParameters
import com.esri.arcgisruntime.tasks.networkanalysis.RouteResult
import com.esri.arcgisruntime.tasks.networkanalysis.RouteTask
import com.esri.arcgisruntime.tasks.networkanalysis.Stop
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.esri.arcgisruntime.geometry.*
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.mapping.view.LocationDisplay
import com.esri.arcgisruntime.navigation.RouteTracker
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol
import com.gis.landsurveyor.routing.GpsUtils
import com.gis.landsurveyor.routing.LocationViewModel
import kotlinx.android.synthetic.main.activity_navigate.*
import java.util.ArrayList
import java.util.concurrent.ExecutionException

class NavigateActivity : AppCompatActivity() {

    private var mTextToSpeech: TextToSpeech? = null
    private var mIsTextToSpeechInitialized = false
    private var mRouteAheadGraphic: Graphic? = null
    private var mRouteTraveledGraphic: Graphic? = null
    private var mRouteTracker: RouteTracker? = null
    private lateinit var loadingDialog: AlertDialog
    private lateinit var locationViewModel: LocationViewModel
    private var isGPSEnabled = false
    private lateinit var startPoint: Point
    private var destinationLat = 0.0
    private var destinationLong = 0.0
    private val picGraphicsOverlay = GraphicsOverlay()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigate)
        showLoadDialog()
        destinationLat = intent.getDoubleExtra("latitude",0.0)
        destinationLong = intent.getDoubleExtra("longitude",0.0)


//        ArcGISRuntimeEnvironment.setLicense(resources.getString(R.string.arcgis_license_key))
        val map = ArcGISMap(Basemap.createStreetsVector())
        mapView.isAttributionTextVisible = false
        mapView.map = map
        mapView.graphicsOverlays.add(picGraphicsOverlay)

        getLocation()
        invokeLocationAction()
        navigate()

        exitButton.setOnClickListener {
            finish()
        }

        recenterButton.isEnabled = false
        recenterButton.setOnClickListener(View.OnClickListener { v: View? ->
            mapView.locationDisplay.autoPanMode = LocationDisplay.AutoPanMode.NAVIGATION
            recenterButton.isEnabled = false
        })

    }

    private fun getLocation() {
        locationViewModel = ViewModelProviders.of(this).get(LocationViewModel::class.java)
        GpsUtils(this).turnGPSOn(object : GpsUtils.OnGpsListener {

            override fun gpsStatus(isGPSEnable: Boolean) {
                this@NavigateActivity.isGPSEnabled = isGPSEnable
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GPS_REQUEST) {
                isGPSEnabled = true
                invokeLocationAction()
            }
        }
    }

    private fun invokeLocationAction() {
        when {
            !isGPSEnabled -> Toast.makeText(this, getString(R.string.enable_gps),Toast.LENGTH_SHORT).show()

            isPermissionsGranted() -> startLocationUpdate()

            shouldShowRequestPermissionRationale() -> Toast.makeText(this, getString(R.string.permission_request),Toast.LENGTH_SHORT).show()

            else -> ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_REQUEST
            )
        }
    }

    private fun startLocationUpdate() {
        locationViewModel.getLocationData().observe(this, Observer {
            setStop(it.longitude, it.latitude)
        })
    }

    private fun setStop(longitude: Double, latitude: Double) {
        startPoint = Point(longitude,latitude, SpatialReferences.getWgs84())
    }

    private fun isPermissionsGranted() =
        ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

    private fun shouldShowRequestPermissionRationale() =
        ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) && ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_REQUEST -> {
                invokeLocationAction()
            }
        }
    }

    private fun navigate() {
        // create a graphics overlay to hold our route graphics
        val graphicsOverlay = GraphicsOverlay()
        mapView.graphicsOverlays.add(graphicsOverlay)

        // initialize text-to-speech to replay navigation voice guidance
        mTextToSpeech = TextToSpeech(this, OnInitListener { status: Int ->
            if (status != TextToSpeech.ERROR) {
                mTextToSpeech!!.language = Resources.getSystem().configuration.locale
                mIsTextToSpeechInitialized = true
            }
        })

        // clear any graphics from the current graphics overlay
        mapView.graphicsOverlays[0].graphics.clear()

        // generate a route with directions and stops for navigation
        val routeTask = RouteTask(this, getString(R.string.routing_service_url2))
        val routeParametersFuture = routeTask.createDefaultParametersAsync()

        routeParametersFuture.addDoneListener {
            try { // define the route parameters
                val routeParameters = routeParametersFuture.get()
                routeParameters.setStops(getStops())
                routeParameters.isReturnDirections = true
                routeParameters.isReturnStops = true
                routeParameters.isReturnRoutes = true
                val routeResultFuture = routeTask.solveRouteAsync(routeParameters)
                routeParametersFuture.addDoneListener {
                    try { // get the route geometry from the route result
                        val routeResult = routeResultFuture.get()
                        val routeGeometry = routeResult.routes[0].routeGeometry
                        // create a graphic for the route geometry
                        val routeGraphic = Graphic(routeGeometry,
                            SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.GRAY, 5f)
                        )
                        // add it to the graphics overlay
                        mapView.graphicsOverlays[0].graphics.add(routeGraphic)
                        // set the map view view point to show the whole route
                        mapView.setViewpointAsync(Viewpoint(routeGeometry.extent))
                        // create a button to start navigation with the given route
//                        val navigateRouteButton = findViewById<ImageButton>(R.id.navigateRouteButton)
//                        navigateRouteButton?.setOnClickListener { v: View? -> startNavigation(routeTask, routeParameters, routeResult) }
                        // start navigating
                        loadingDialog.dismiss()
                        startNavigation(routeTask, routeParameters, routeResult)
                    } catch (e: ExecutionException) {
                        val error = "Error creating default route parameters1: " + e.message
                        Toast.makeText(this, error, Toast.LENGTH_LONG).show()
                        Log.e("chikk", error)
                    } catch (e: InterruptedException) {
                        val error = "Error creating default route parameters2: " + e.message
                        Toast.makeText(this, error, Toast.LENGTH_LONG).show()
                        Log.e("chikk", error)
                    }
                }
            } catch (e: InterruptedException) {
                val error = "Error getting the route result1 " + e.message
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
                Log.e("chikk", error)
            } catch (e: ExecutionException) {
                val error = "Error getting the route result2 " + e.message
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
                Log.e("chikk", error)
            }
        }
    }

    private fun getStops(): List<Stop>? {
        val stops: ArrayList<Stop> = ArrayList(2)
        val current = Stop(startPoint)
        stops?.add(current)
        // USS San Diego Memorial
        val destination = Stop(Point(destinationLong, destinationLat, SpatialReferences.getWgs84()))
        stops?.add(destination)
        return stops
    }

    private fun startNavigation(routeTask: RouteTask, routeParameters: RouteParameters, routeResult: RouteResult) { // clear any graphics from the current graphics overlay
        var remainingTimeString:String
        mapView.graphicsOverlays[0].graphics.clear()
        // get the route's geometry from the route result
        val routeGeometry = routeResult.routes[0].routeGeometry
        createGraphicsOverlay(routeResult.routes[0].stops.last().geometry.toWgs84())
        // create a graphic (with a dashed line symbol) to represent the route
        mRouteAheadGraphic = Graphic(routeGeometry,
            SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 5f)
        )
        mapView.graphicsOverlays[0].graphics.add(mRouteAheadGraphic)
        // create a graphic (solid) to represent the route that's been traveled (initially empty)
        mRouteTraveledGraphic = Graphic(routeGeometry,
            SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.GRAY, 5f)
        )
        mapView.graphicsOverlays[0].graphics.add(mRouteTraveledGraphic)
        // get the map view's location display
        val locationDisplay: LocationDisplay = mapView.locationDisplay

//        //อาจจะไม่ต้องใช้ 2 อันนี้ เพราะเราไม่ต้อง simulate
//        // set up a simulated location data source which simulates movement along the route
//        mSimulatedLocationDataSource = SimulatedLocationDataSource(routeGeometry)
//        // set the simulated location data source as the location data source for this app
//        locationDisplay.locationDataSource = mSimulatedLocationDataSource

        locationDisplay.autoPanMode = LocationDisplay.AutoPanMode.NAVIGATION
        // if the user navigates the map view away from the location display, activate the recenter button
        locationDisplay.addAutoPanModeChangedListener { recenterButton.isEnabled = true }
        // set up a RouteTracker for navigation along the calculated route
        mRouteTracker = RouteTracker(this, routeResult, 0)
        mRouteTracker?.enableReroutingAsync(routeTask, routeParameters,
            RouteTracker.ReroutingStrategy.TO_NEXT_WAYPOINT, true)
        // get a reference to navigation text views
        val distanceRemainingTextView = findViewById<TextView>(R.id.distanceRemainingTextView)
        val timeRemainingTextView = findViewById<TextView>(R.id.timeRemainingTextView)
//        val nextDirectionTextView = findViewById<TextView>(R.id.nextDirectionTextView)


        // listen for changes in location
        locationDisplay.addLocationChangedListener { locationChangedEvent: LocationDisplay.LocationChangedEvent ->
            // track the location and update route tracking status
            val trackLocationFuture: ListenableFuture<Void>? = mRouteTracker?.trackLocationAsync(locationChangedEvent.location)
            trackLocationFuture?.addDoneListener {
                // listen for new voice guidance events
                mRouteTracker?.addNewVoiceGuidanceListener { newVoiceGuidanceEvent: RouteTracker.NewVoiceGuidanceEvent ->
                    // use Android's text to speech to speak the voice guidance
                    speakVoiceGuidance(newVoiceGuidanceEvent.voiceGuidance.text)
//                    nextDirectionTextView?.text = getString(R.string.next_direction, newVoiceGuidanceEvent.voiceGuidance.text)
                }
                // get the route's tracking status
                val trackingStatus: TrackingStatus? = mRouteTracker?.trackingStatus
                // set geometries for the route ahead and the remaining route
                mRouteAheadGraphic?.geometry = trackingStatus?.routeProgress?.remainingGeometry
                mRouteTraveledGraphic?.geometry = trackingStatus?.routeProgress?.traversedGeometry
                // get remaining distance information
                val remainingDistance = trackingStatus?.destinationProgress?.remainingDistance
                // covert remaining minutes to hours:minutes:seconds
                remainingTimeString = if (trackingStatus != null){DateUtils
                    .formatElapsedTime(((trackingStatus.destinationProgress?.remainingTime)!! * 60).toLong())}else{"wait"}
                // update text views
                distanceRemainingTextView?.text = getString(R.string.distance_remaining, remainingDistance?.displayText,
                    remainingDistance?.displayTextUnits?.pluralDisplayName)
                timeRemainingTextView?.text = getString(R.string.time_remaining, remainingTimeString)
                // if a destination has been reached
                if (trackingStatus?.destinationStatus == DestinationStatus.REACHED) { // if there are more destinations to visit. Greater than 1 because the start point is considered a "stop"
                    if (mRouteTracker?.trackingStatus?.remainingDestinationCount!! > 1) { // switch to the next destination
                        mRouteTracker!!.switchToNextDestinationAsync()
                        Toast.makeText(this, "Navigating to the second stop, the Fleet Science Center.", Toast.LENGTH_LONG).show()
                    } else { // the final destination has been reached, stop the simulated location data source
//                        mSimulatedLocationDataSource!!.onStop()
                        Toast.makeText(this, "Arrived at the final destination.", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        // start the LocationDisplay, which starts the SimulatedLocationDataSource
        locationDisplay.startAsync()
        Toast.makeText(this, "Navigating start", Toast.LENGTH_LONG).show()
    }


    private fun speakVoiceGuidance(voiceGuidanceText: String) {
        if (mIsTextToSpeechInitialized && !mTextToSpeech!!.isSpeaking) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mTextToSpeech!!.speak(voiceGuidanceText, TextToSpeech.QUEUE_FLUSH, null, null)
            } else {
                mTextToSpeech!!.speak(voiceGuidanceText, TextToSpeech.QUEUE_FLUSH, null)
            }
        }
    }

    private fun createGraphicsOverlay(point: Point){
        picGraphicsOverlay.graphics.clear()
        val picSymbol = ContextCompat.getDrawable(this, R.drawable.pin) as BitmapDrawable?
        try {
            // Create Picture Symbol
            val pinSourceSymbol: PictureMarkerSymbol = PictureMarkerSymbol.createAsync(picSymbol).get()
//            val pinSourceSymbol: PictureMarkerSymbol = PictureMarkerSymbol("http://sampleserver6.arcgisonline.com/arcgis/rest/services/Recreation/FeatureServer/0/images/e82f744ebb069bb35b234b3fea46deae")

            pinSourceSymbol.height = 36F
            pinSourceSymbol.width = 36F
            // Sey Y position of picture from ground
            pinSourceSymbol.offsetY = 18F
            // Load Picture
            pinSourceSymbol.loadAsync()
            // Set Callback
            pinSourceSymbol.addDoneLoadingListener {
                // When Picture is loaded,
                // Create New Graphic with Picture Symbol
                val picGraphic = Graphic(point, pinSourceSymbol)
                picGraphicsOverlay?.graphics?.add(picGraphic)
            }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }
    }


    private fun showLoadDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.loading_dialog,null)
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setView(dialogView)
        dialogBuilder.setCancelable(false)

        loadingDialog = dialogBuilder.create()
        loadingDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        loadingDialog.show()
    }

    fun Point.toWgs84(): Point = GeometryEngine.project(this, SpatialReferences.getWgs84()) as Point
//    fun Point.toWebMercator(): Point = GeometryEngine.project(this, SpatialReferences.getWebMercator()) as Point
//    fun Polyline.toWgs84(): Polyline = GeometryEngine.project(this, SpatialReferences.getWgs84()) as Polyline
//    fun Polyline.toWebMercator(): Polyline = GeometryEngine.project(this, SpatialReferences.getWebMercator()) as Polyline
//    fun Polygon.toWgs84(): Polygon = GeometryEngine.project(this, SpatialReferences.getWgs84()) as Polygon
//    fun Polygon.toWebMercator(): Polygon = GeometryEngine.project(this, SpatialReferences.getWebMercator()) as Polygon

}

const val LOCATION_REQUEST = 100
const val GPS_REQUEST = 101