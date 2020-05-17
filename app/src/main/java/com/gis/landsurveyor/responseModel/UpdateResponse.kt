package com.gis.landsurveyor.responseModel

import com.google.gson.annotations.SerializedName

data class UpdateResponse (
    @SerializedName("message")
    val message:String? = null
)