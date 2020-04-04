package com.gis.landsurveyor

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.esri.arcgisruntime.geometry.*
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Basemap
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.mapping.view.LocationDisplay
import com.esri.arcgisruntime.symbology.SimpleFillSymbol
import com.esri.arcgisruntime.symbology.SimpleLineSymbol
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol
import com.gis.landsurveyor.responseModel.RequestModel
import kotlinx.android.synthetic.main.fragment_map.*
import java.text.DecimalFormat
import java.util.*
import kotlin.math.absoluteValue

/**
 * A simple [Fragment] subclass.
 */
class MapFragment : Fragment() {
    lateinit var request: RequestModel

    private var longitude: Double = 0.0
    private var latitude: Double = 0.0
    private var DeedID: Int = 0
    private var area: Double = 0.0
    private val df = DecimalFormat("#.####")
    private var mGraphicsOverlay: GraphicsOverlay? = null
    private var pointGraphicsOverlay: GraphicsOverlay? = null
    private lateinit var logStack: Stack<Point>
    var polygonPoints = PointCollection(SpatialReferences.getWgs84())
    private var polygon: Polygon? = null

    val polygonSymbol = SimpleFillSymbol(
        SimpleFillSymbol.Style.SOLID, Color.argb(50,226, 119, 40),
        SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 2.0f)
    )

    val mPointSymbol = SimpleMarkerSymbol(SimpleMarkerSymbol.Style.SQUARE, Color.RED, 15F)

    val centPointSymbol = SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CROSS, Color.MAGENTA, 20F)
    lateinit var navController: NavController

    lateinit var mLocationDisplay: LocationDisplay
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        request = HomeActivity.currentRequestModel
        val latitude = request.latitude!!
        val longitude = request.longitude!!
        navController = findNavController()


        centPointSymbol.outline = SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.CYAN, 2.0f)
        logStack = Stack()
        val map = ArcGISMap(Basemap.Type.TOPOGRAPHIC, latitude, longitude, 16)
        mapView.map = map

        createGraphicsOverlay()
        createPointGraphics()

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
                    Log.d("chikk", "AREA = $area")
                    toSummaryPage()
                }
            }
            Log.d("chikk", "Finish at first")
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

        val action = MapFragmentDirections.actionMapFragmentToSavedFragment()
//        action.areaArgs = area.toFloat()
        navController.navigate(R.id.action_mapFragment_to_savedFragment)
    }

    fun createGraphicsOverlay(){
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
        Log.d(
            "chikkkkkkk",
            "Lat: " + String.format("%.4f", wgs84Point.y) + ", Lon: " + String.format(
                "%.4f",
                wgs84Point.x
            )
        )
        polygonPoints.add(Point(wgs84Point.x, wgs84Point.y))

//        val mPointSymbol = SimpleMarkerSymbol(SimpleMarkerSymbol.Style.DIAMOND, -0xFFFFFF, 20F)
//            mPointSymbol.outline = SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 2.0f)
//        val mPointGraphic = Graphic(points, mPointSymbol)
//        mPointGraphicsOverlay!!.graphics.add(mPointGraphic)
    }

    fun createPointGraphics(){
//        val point = Point(latitude,longitude, SpatialReferences.getWgs84())
        val point = Point(
            longitude, latitude,
            SpatialReferences.getWgs84()
        )

        val pointGraphic = Graphic(point, centPointSymbol)
        pointGraphicsOverlay?.graphics?.add(pointGraphic)
        Log.d("chikk", "la long = doneeeee")

    }

}
