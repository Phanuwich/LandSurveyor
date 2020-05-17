package com.gis.landsurveyor.apiservice

import com.gis.landsurveyor.requestModel.LoginRequest
import com.gis.landsurveyor.requestModel.LoginResponse
import com.gis.landsurveyor.requestModel.UpdateRequest
import com.gis.landsurveyor.responseModel.*
import retrofit2.Call
import retrofit2.http.*
import io.reactivex.Observable

interface ApiService {



    @Headers("Content-Type: application/json")
    @POST("/api/v1/login")
    fun userLogin(
        @Body request_body: LoginRequest
    ):Call<LoginResponse>

    @GET("/api/v1/configs")
    fun getConfig(
        @Query("user_id") user_id:Int?
    ):Call<ConfigModel>

    @GET("/api/v1/deeds")
    fun getRequests(
        @Query("employee_id") employee_id:Int?,
        @Query("status_id") status_id:String = "not:5",
        @Query("sort") sort: String ="status_id,updated_date"
        ):Call<MutableList<RequestModel>>

    @GET("/api/v1/deeds")
    fun getRequestsTest(
        @QueryMap body: HashMap<String, Any>
    ):Observable<MutableList<RequestModel>>

//    @GET(ServiceURL.NEAR_COV)
//    @Headers("Referer: COVID19SelfTracking")
//    fun callNearCovid(@QueryMap body: HashMap<String, Any>, @Header("Authorization") authorization: String): Observable<Response<Void>>

    @GET("/api/v1/deeds/{id}")
    fun getDetail(
        @Path("id") id: Int
    ):Call<RequestModel>

    @GET("/api/v1/deedType")
    fun getDeedType(
    ):Call<ArrayList<DeedType>>

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