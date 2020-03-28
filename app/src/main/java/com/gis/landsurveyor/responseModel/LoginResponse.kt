package com.gis.landsurveyor.responseModel

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("message")
    val message: String?,
    val user_id: Int
)