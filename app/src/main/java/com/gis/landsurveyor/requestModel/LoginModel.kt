package com.gis.landsurveyor.requestModel

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("role")
    val role:String
)

data class LoginResponse(
    @SerializedName("message")
    val message: String?,
    @SerializedName("user_id")
    val user_id: Int
)