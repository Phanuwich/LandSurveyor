package com.gis.landsurveyor.responseModel

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RequestModel(
    val deed_id : Int,
    val customer : Customer,
    val employee_id: Int,
    val deed_name: String,
    val image_path:ArrayList<String>,
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


@Parcelize
data class Customer (
    val customer_id:Int,
    val user_id:Int,
    val first_name:String,
    val last_name:String,
    val phone_number:String
): Parcelable