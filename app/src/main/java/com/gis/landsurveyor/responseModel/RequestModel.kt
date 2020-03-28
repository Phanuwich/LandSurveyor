package com.gis.landsurveyor.responseModel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RequestModel(
    val deed_id : Int,
    val customer_id: Int,
    val employee_id: Int,
    val deed_name: String,
    val image_path:String?,
    val address:String?,
    val latitude:Double,
    val longitude:Double?,
    val area:Double?,
    val deed_number:String?,
    val deed_status:String?,
    val status:String,
    val created_date:String,
    val updated_date:String?
) :Parcelable