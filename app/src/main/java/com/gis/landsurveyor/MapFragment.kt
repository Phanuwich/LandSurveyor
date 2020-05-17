package com.gis.landsurveyor

import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment
import com.esri.arcgisruntime.geometry.*
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Basemap
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.mapping.view.LocationDisplay
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol
import com.esri.arcgisruntime.symbology.SimpleFillSymbol
import com.esri.arcgisruntime.symbology.SimpleLineSymbol
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol
import com.gis.landsurveyor.responseModel.RequestModel
import kotlinx.android.synthetic.main.fragment_map.*
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.ExecutionException
import kotlin.math.absoluteValue
import kotlin.math.round

/**
 * A simple [Fragment] subclass.
 */
class MapFragment : Fragment() {
    lateinit var request: RequestModel

    private var longitude: Double = 0.0
    private var latitude: Double = 0.0
    private var area: Double = 0.0
    private var mGraphicsOverlay: GraphicsOverlay? = null
    private var pointGraphicsOverlay: GraphicsOverlay? = null
    private lateinit var logStack: Stack<Point>
    var polygonPoints = PointCollection(SpatialReferences.getWgs84())
    private var polygon: Polygon? = null

    private val polygonSymbol = SimpleFillSymbol(
        SimpleFillSymbol.Style.SOLID, Color.argb(50,226, 119, 40),
        SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 2.0f)
    )

    val mPointSymbol = SimpleMarkerSymbol(SimpleMarkerSymbol.Style.SQUARE, Color.RED, 10F)

    lateinit var centPointSymbol: PictureMarkerSymbol
    lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HomeActivity.titleContainer.visibility = View.GONE
        request = HomeActivity.currentRequestModel
        latitude = request.latitude!!
        longitude = request.longitude!!
        navController = findNavController()
        logStack = Stack()


        ArcGISRuntimeEnvironment.setLicense(resources.getString(R.string.arcgis_license_key))
        val map = ArcGISMap(Basemap.Type.TOPOGRAPHIC_VECTOR, latitude, longitude, 16)
        mapView.isAttributionTextVisible = false
        mapView.map = map

        createGraphicsOverlay()
        createPointGraphics()
        if (polygonPoints.isNotEmpty()){addPolygon()}

        pointer.setOnClickListener {
            getLocation()
            addPolygon()
        }

        undoBtn.setOnClickListener {

            if (!polygonPoints.isEmpty()) {
                logStack?.push(polygonPoints.last())
                polygonPoints.remove(polygonPoints.last())
                addPolygon()
            }
        }

        redoBtn.setOnClickListener {
            if (!logStack.empty()) {
                polygonPoints.add(logStack!!.lastElement())
                logStack!!.pop()
                addPolygon()
            }
        }

        finishedBtn.setOnClickListener {
            if (!polygonPoints!!.isEmpty()) {
                area = GeometryEngine.areaGeodetic(
                    polygon,
                    AreaUnit(AreaUnitId.SQUARE_METERS),
                    GeodeticCurveType.GEODESIC
                ).toDouble().absoluteValue
                if (area != 0.0) {
                    area = round(area * 1000)/1000
                    toSummaryPage()
                }
            }
        }

    }

    private fun addPolygon() {
        mGraphicsOverlay?.graphics?.clear()
        polygon = Polygon(polygonPoints,SpatialReferences.getWgs84())

        val polygonGraphic = Graphic(polygon, polygonSymbol)
        mGraphicsOverlay!!.graphics.add(polygonGraphic)


        for (i in 0 until polygonPoints.size){
            val mPointGraphic = Graphic(polygonPoints[i], mPointSymbol)
            mGraphicsOverlay!!.graphics.add(mPointGraphic)
        }
    }

    private fun toSummaryPage() {
        val bundle = Bundle()
        bundle.putDouble("AREA",area)
        navController.navigate(R.id.action_mapFragment_to_savedFragment,bundle)
    }

    private fun createGraphicsOverlay(){
        pointGraphicsOverlay = GraphicsOverlay()
        mapView!!.graphicsOverlays.add(pointGraphicsOverlay)
        mGraphicsOverlay = GraphicsOverlay()
        mapView!!.graphicsOverlays.add(mGraphicsOverlay)
    }

    private fun getLocation() {
        val lox = (targetPoint.left + targetPoint.right)/2
        val loy = (targetPoint.bottom + targetPoint.top)/2

        val screenPoint = android.graphics.Point(lox, loy)
        val mapPoint = mapView.screenToLocation(screenPoint)
        val wgs84Point = GeometryEngine.project(mapPoint, SpatialReferences.getWgs84()) as Point
        polygonPoints.add(Point(wgs84Point.x, wgs84Point.y))
    }

    private fun createPointGraphics(){
        val picSymbol = ContextCompat.getDrawable(context!!, R.drawable.pin) as BitmapDrawable?
        centPointSymbol = PictureMarkerSymbol.createAsync(picSymbol).get()
        val point = Point(
            longitude, latitude,
            SpatialReferences.getWgs84()
        )

        try {
            // Create Picture Symbol
            centPointSymbol = PictureMarkerSymbol.createAsync(picSymbol).get()
//            val pinSourceSymbol: PictureMarkerSymbol = PictureMarkerSymbol("http://sampleserver6.arcgisonline.com/arcgis/rest/services/Recreation/FeatureServer/0/images/e82f744ebb069bb35b234b3fea46deae")

            centPointSymbol.height = 28F
            centPointSymbol.width = 28F
            centPointSymbol.offsetY = 14F
            // Load Picture
            centPointSymbol.loadAsync()

            centPointSymbol.addDoneLoadingListener {
                val pointGraphic = Graphic(point, centPointSymbol)
                pointGraphicsOverlay?.graphics?.add(pointGraphic)
            }
        }catch (e: InterruptedException){
            e.printStackTrace()
        }catch (e: ExecutionException) {
            e.printStackTrace()
        }
    }

}
