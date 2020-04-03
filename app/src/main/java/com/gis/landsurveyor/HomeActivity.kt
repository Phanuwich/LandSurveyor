package com.gis.landsurveyor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gis.landsurveyor.responseModel.RequestModel
import com.esri.arcgisruntime.geometry.Point
import kotlin.properties.Delegates

class HomeActivity : AppCompatActivity() {


    companion object {
        lateinit var startPoint: Point
        lateinit var currentRequestModel: RequestModel
        var currentRequest by Delegates.notNull<Int>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
//        supportActionBar!!.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val titleFragment = TitleFragment()
        supportFragmentManager.beginTransaction().replace(R.id.title_bar_fragment,titleFragment,titleFragment.tag).commit()

    }

//    private fun getLocation() {
//        locationViewModel = ViewModelProviders.of(this).get(LocationViewModel::class.java)
//        GpsUtils(this).turnGPSOn(object : GpsUtils.OnGpsListener {
//
//            override fun gpsStatus(isGPSEnable: Boolean) {
//                this@HomeActivity.isGPSEnabled = isGPSEnable
//            }
//        })
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == Activity.RESULT_OK) {
//            if (requestCode == GPS_REQUEST) {
//                isGPSEnabled = true
//                invokeLocationAction()
//            }
//        }
//    }
//
//    private fun invokeLocationAction() {
//        when {
//            !isGPSEnabled -> d("chikk", "invoke gps ${getString(R.string.enable_gps)} ")
//
//            isPermissionsGranted() -> startLocationUpdate()
//
//            shouldShowRequestPermissionRationale() -> d("chikk", "invoke show ${getString(R.string.permission_request)} ")
//
//            else -> ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
//                LOCATION_REQUEST
//            )
//        }
//    }
//
//    private fun startLocationUpdate() {
//        locationViewModel.getLocationData().observe(this, Observer {
//            setStop(it.longitude, it.latitude)
////            latLong.text =  getString(R.string.latLong, it.longitude, it.latitude)
//        })
//    }
//
//    private fun setStop(longitude: Double, latitude: Double) {
//        startPoint = Point(longitude,latitude, SpatialReferences.getWgs84())
//        d("chikk","starttt rrr = $startPoint")
//    }
//
//    private fun isPermissionsGranted() =
//        ActivityCompat.checkSelfPermission(
//            this,
//            Manifest.permission.ACCESS_FINE_LOCATION
//        ) == PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(
//                    this,
//                    Manifest.permission.ACCESS_COARSE_LOCATION
//                ) == PackageManager.PERMISSION_GRANTED
//
//    private fun shouldShowRequestPermissionRationale() =
//        ActivityCompat.shouldShowRequestPermissionRationale(
//            this,
//            Manifest.permission.ACCESS_FINE_LOCATION
//        ) && ActivityCompat.shouldShowRequestPermissionRationale(
//            this,
//            Manifest.permission.ACCESS_COARSE_LOCATION
//        )
//
//    @SuppressLint("MissingPermission")
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        when (requestCode) {
//            LOCATION_REQUEST -> {
//                invokeLocationAction()
//            }
//        }
//    }

}

