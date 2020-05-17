package com.gis.landsurveyor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log.d
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.gis.landsurveyor.apiservice.RetrofitClient
import com.gis.landsurveyor.requestModel.LoginRequest
import com.gis.landsurveyor.requestModel.LoginResponse
import com.gis.landsurveyor.responseModel.ConfigModel
import com.gis.landsurveyor.responseModel.EmployeeInfo
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {
    private val apiService = RetrofitClient.callApi()
    lateinit var empInfo: EmployeeInfo
    lateinit var loadingDialog: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
//        supportActionBar!!.hide()
        this.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        this.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        login_btn.setOnClickListener {
            val username = username.text.toString()
            val password = password.text.toString()
            showLoadDialog()
            callLogin(username,password)
        }
    }

    private fun callLogin(username: String, password: String) {
        val requestObj = LoginRequest(username,password,getString(R.string.login_role))
        apiService.userLogin(requestObj).enqueue(object : Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                loadingDialog.dismiss()
                Toast.makeText(this@LoginActivity, getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.body()?.user_id == null) {
                    loadingDialog.dismiss()
                    Toast.makeText(this@LoginActivity, getString(R.string.error_login), Toast.LENGTH_SHORT).show()
                }else{
                    callConfig(response.body()?.user_id)
                }
            }
        })
    }

    fun callConfig(user_id: Int?) {
        apiService.getConfig(user_id).enqueue(object : Callback<ConfigModel>{
            override fun onFailure(call: Call<ConfigModel>, t: Throwable) {
                loadingDialog.dismiss()
                Toast.makeText(this@LoginActivity, getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<ConfigModel>, response: Response<ConfigModel>) {
                if (response.body()?.user == null) {
                    Toast.makeText(this@LoginActivity, getString(R.string.error_config), Toast.LENGTH_SHORT).show()
                }else{
                    SingletonConfig.instance = response.body()!!
                    val intent = Intent(applicationContext, HomeActivity::class.java)
                    finish()
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    loadingDialog.dismiss()
                    startActivity(intent)
                }
            }
        })
    }

    private fun showLoadDialog() {
        loadingDialog = LoadDialog.showLoadDialog(this)
        loadingDialog.show()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
