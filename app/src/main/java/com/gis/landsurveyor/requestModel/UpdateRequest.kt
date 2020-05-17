package com.gis.landsurveyor.requestModel

import com.google.gson.annotations.SerializedName

data class UpdateRequest (
    @SerializedName("employee_id")
    val employee_id: Int? = null,
    @SerializedName("area")
    val area: Number? = null,
    @SerializedName("deed_number")
    val deed_number: String? = null,
    @SerializedName("deed_type_id")
    val deed_type_id: Int? = null,
    @SerializedName("status_id")
    val status_id: Int? = null
)