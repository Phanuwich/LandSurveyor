package com.gis.landsurveyor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Log.d
import com.gis.landsurveyor.apiservice.RetrofitClient
import com.gis.landsurveyor.responseModel.EmployeeInfo
import com.gis.landsurveyor.responseModel.RequestModel
import kotlinx.android.synthetic.main.activity_menu.*
import kotlinx.android.synthetic.main.title_bar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuActivity : AppCompatActivity() {
    private val retrofitClient: RetrofitClient = RetrofitClient()
    private val apiService = retrofitClient.callApi()
    lateinit var empInfo: EmployeeInfo

    private val header : MutableList<String> = ArrayList()
    val body : MutableList<MutableList<RequestModel>> = ArrayList()
    private val tmpBody : MutableList<RequestModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar!!.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val tmpHeaders =  resources.getStringArray(R.array.statusList)
        tmpHeaders.forEach { header.add(it) }
        d("chikk","header $header")

        //Receive employee data
        empInfo = SingletonEmp.instance!!
        val empName = getString(R.string.emp_name_label,empInfo.first_name , empInfo.last_name)
        emp_name_label.text = empName
        d("chikk","empName $empName")



        callRequests(empInfo.employee_id)

    }



    private fun callRequests(emp_id: Int?) {
        Log.d("chikk", "start again")
        apiService.getRequests(emp_id).enqueue(object : Callback<ArrayList<RequestModel>> {
            override fun onFailure(call: Call<ArrayList<RequestModel>>, t: Throwable) {
                Log.d("chikk", "${getString(R.string.error_request_list)}: $t")
            }
            override fun onResponse(call: Call<ArrayList<RequestModel>>, response: Response<ArrayList<RequestModel>>) {
                if (response.body()?.size == 0) {
                    Log.d("chikk", getString(R.string.empty_request_list))
                }else{
//                    for (i in 0 until (response.body()!!.size)) {
//                        Log.d(
//                            "chikk",
//                            "Query Request success index $i = ${response.body()!![i].deed_name}"
//                        )
//                    }
                    val list = response.body()!!.groupBy { it.status }
                    makBodyTemp()
                    for (i in 0 until list.size){
                        d("chikk","round $i")
                        if (list.keys.elementAt(i)==header[0]){
                            body[0] = list[list.keys.elementAt(i)] as MutableList<RequestModel>
                        }
                        if (list.keys.elementAt(i)==header[1]){
                            body[1] = list[list.keys.elementAt(i)] as MutableList<RequestModel>
                        }
                        if (list.keys.elementAt(i)==header[2]){
                            body[2] = list[list.keys.elementAt(i)] as MutableList<RequestModel>
                        }
                        d("chikk","Body $body")
                    }
                    expandableListView.setAdapter(MyExpandableListAdapter(this@MenuActivity,expandableListView,header,body))
                }
            }
        })
    }
    private fun makBodyTemp(){
        body.add(tmpBody)
        body.add(tmpBody)
        body.add(tmpBody)
    }
}
