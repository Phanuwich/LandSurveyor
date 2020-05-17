package com.gis.landsurveyor.routing

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.gis.landsurveyor.routing.LocationLiveData

class LocationViewModel(application: Application) : AndroidViewModel(application) {

    private val locationData = LocationLiveData(application)

    fun getLocationData() = locationData
}