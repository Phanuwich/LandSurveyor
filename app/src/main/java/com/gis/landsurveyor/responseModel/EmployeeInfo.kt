package com.gis.landsurveyor.responseModel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EmployeeInfo (
    val employee_id:Int,
    val user_id:Int,
    val first_name:String,
    val last_name:String
): Parcelable