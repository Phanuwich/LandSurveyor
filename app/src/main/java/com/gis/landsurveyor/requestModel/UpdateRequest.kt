package com.gis.landsurveyor.requestModel

data class UpdateRequest (
//    "employee_id": 0,
////    "area": 0,
////    "deed_number": "string",
////    "deed_status": "string",
////    "status": "assigned"
    val employee_id: Int? = null,
    val area: Number? = null,
    val deed_number: String? = null,
    val deed_status: String? = null,
    val status: String? = null
)