package com.gis.landsurveyor.responseModel

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RequestModel(
    @SerializedName("deed_id")
    val deed_id : Int,
    @SerializedName("customer")
    val customer : Customer,
    @SerializedName("deed_name")
    val deed_name: String,
    @SerializedName("image_path")
    val image_path: ArrayList<String>,
    @SerializedName("address")
    val address:String?,
    @SerializedName("latitude")
    val latitude:Double,
    @SerializedName("longitude")
    val longitude:Double?,
    @SerializedName("area")
    val area:Double?,
    @SerializedName("deed_number")
    val deed_number:String?,
    @SerializedName("deed_type")
    val deed_status:DeedType?,
    @SerializedName("status_id")
    val status_id:Int,
    @SerializedName("created_date")
    val created_date:String,
    @SerializedName("updated_date")
    val updated_date:String?
) :Parcelable


@Parcelize
data class Customer (
    @SerializedName("customer_id")
    val customer_id:Int,
    @SerializedName("user_id")
    val user_id:Int,
    @SerializedName("first_name")
    val first_name:String,
    @SerializedName("last_name")
    val last_name:String,
    @SerializedName("phone_number")
    val phone_number:String
): Parcelable



