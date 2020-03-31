package com.gis.landsurveyor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.gis.landsurveyor.apiservice.RetrofitClient
import com.gis.landsurveyor.requestModel.LoginRequest
import com.gis.landsurveyor.responseModel.EmployeeInfo
import com.gis.landsurveyor.responseModel.LoginResponse
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {
    private val retrofitClient: RetrofitClient = RetrofitClient()
    private val apiService = retrofitClient.callApi()
    lateinit var empInfo: EmployeeInfo
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar!!.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        login_btn.setOnClickListener {
            val username = username.text.toString()
            val password = password.text.toString()
            callLogin(username,password)
        }
    }

    private fun callLogin(username: String, password: String) {
        val requestObj = LoginRequest(username,password)
        apiService.userLogin(requestObj).enqueue(object : Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.d("chikk", "Failure jaaa ${t}")
            }
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
//                d("chikk","status = ${response.message()}")
                if (response.body()?.user_id == null) {
                    Log.d("chikk", getString(R.string.error_login))
                }else{
                    Log.d("chikk", "Login success user id = ${response.body()?.user_id}")
                    callEmployee(response.body()?.user_id)
                }
            }
        })
    }

    fun callEmployee(user_id: Int?) {
        apiService.getEmpInfo(user_id).enqueue(object : Callback<EmployeeInfo>{
            override fun onFailure(call: Call<EmployeeInfo>, t: Throwable) {
                Log.d("chikk", "Failure jaaa $t")
            }
            override fun onResponse(call: Call<EmployeeInfo>, response: Response<EmployeeInfo>) {
                if (response.body()?.employee_id == null) {
                    Log.d("chikk", getString(R.string.error_emp))
                }else{
                    Log.d(
                        "chikk", "Your Employee info = ${response.body()?.employee_id}," +
                                "${response.body()?.user_id}," +
                                "${response.body()?.first_name}," +
                                "${response.body()?.last_name}"
                    )
                    empInfo = response.body()!!
                    SingletonEmp.instance = empInfo
                    val intent = Intent(applicationContext, MenuActivity::class.java)
//                    intent.putExtra("EmpInfo",empInfo)
                    finish()
                    startActivity(intent)
                }
            }
        })
    }
}
