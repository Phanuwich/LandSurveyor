package com.gis.landsurveyor.apiservice

import com.gis.landsurveyor.requestModel.LoginRequest
import com.gis.landsurveyor.requestModel.UpdateRequest
import com.gis.landsurveyor.responseModel.EmployeeInfo
import com.gis.landsurveyor.responseModel.LoginResponse
import com.gis.landsurveyor.responseModel.RequestModel
import com.gis.landsurveyor.responseModel.UpdateResponse
import retrofit2.Call
import retrofit2.http.*
import java.util.ArrayList

interface ApiService {

    @Headers("Content-Type: application/json")
    @POST("/api/v1/login")
    fun userLogin(
        @Body request_body: LoginRequest
    ):Call<LoginResponse>

    @GET("/api/v1/employees")
    fun getEmpInfo(
        @Query("user_id") user_id:Int?
    ):Call<EmployeeInfo>

    @GET("/api/v1/deeds")
    fun getRequests(
        @Query("employee_id") employee_id:Int?
    ):Call<ArrayList<RequestModel>>

    @PATCH("api/v1/deeds/{id}")
    fun updateStatus(
        @Path("id") id: Int,
        @Body updateData: UpdateRequest
    ):Call<UpdateResponse>

    @PATCH("api/v1/deeds/{id}")
    fun updateRequest(
        @Path("id") id: Int,
        @Body updateData: UpdateRequest
    ):Call<UpdateResponse>
}