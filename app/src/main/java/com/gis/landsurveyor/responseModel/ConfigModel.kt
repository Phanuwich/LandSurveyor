package com.gis.landsurveyor.responseModel

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
data class ConfigModel (
    @SerializedName("app_name")
    val app_name:String,
    @SerializedName("user")
    val user:EmployeeInfo,
    @SerializedName("resources")
    val resources:ResourcesConfig
): Parcelable

@Parcelize
data class EmployeeInfo (
    @SerializedName("employee_id")
    val employee_id:Int,
    @SerializedName("user_id")
    val user_id:Int,
    @SerializedName("first_name")
    val first_name:String,
    @SerializedName("last_name")
    val last_name:String
): Parcelable

@Parcelize
data class ResourcesConfig (
    @SerializedName("deeds")
    val deeds:RequestUrl?,
    @SerializedName("status")
    val status:ArrayList<RequestStatus>,
    @SerializedName("deed_type")
    val deed_type:ArrayList<DeedType>
): Parcelable

@Parcelize
data class RequestUrl (
    @SerializedName("url")
    val url:String
): Parcelable

@Parcelize
data class RequestStatus(
    @SerializedName("status_id")
    val status_id:Int,
    @SerializedName("name")
    val name:String
): Parcelable

@Parcelize
data class DeedType(
    @SerializedName("deed_type_id")
    val deed_type_id:Int,
    @SerializedName("name")
    val name:String
): Parcelable