package com.gis.landsurveyor.apiservice

import com.gis.landsurveyor.R
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {

    fun callApi(): ApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(ApiService::class.java)
        return api
    }
}

const val BASE_URL = "http://land-surveyor-dev.eba-atp2ae9g.ap-southeast-1.elasticbeanstalk.com"

