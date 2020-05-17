package com.gis.landsurveyor.apiservice

import android.util.Log
import com.gis.landsurveyor.R
import com.gis.landsurveyor.requestModel.UpdateRequest
import com.gis.landsurveyor.responseModel.RequestModel
import com.gis.landsurveyor.responseModel.UpdateResponse
import com.google.android.gms.common.api.CommonStatusCodes.TIMEOUT
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.TimeUnit

class RetrofitClient {

    companion object {

        private const val TIMEOUT = 900000

        fun callApi(): ApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(ApiService::class.java)
        }

        fun create(): ApiService {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .client(getClient())
                .build()
            return retrofit.create(ApiService::class.java)
        }

        private fun getClient(): OkHttpClient {
            val okHttpClient = OkHttpClient.Builder()
            return okHttpClient
                .readTimeout(TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                .writeTimeout(TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                .connectTimeout(TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                .build()
        }

        fun callRequestService(emp_id: Int, i: Int): Observable<MutableList<RequestModel>> {
            val params = hashMapOf<String, Any>(
                "employee_id" to emp_id,
                "status_id" to i
            )
            return create().getRequestsTest(params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
        }
    }

}

const val BASE_URL = "http://land-surveyor-dev.eba-atp2ae9g.ap-southeast-1.elasticbeanstalk.com"

