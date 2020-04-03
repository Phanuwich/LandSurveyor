package com.gis.landsurveyor

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.Log.d
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.gis.landsurveyor.apiservice.ApiService
import com.gis.landsurveyor.apiservice.BASE_URL
import com.gis.landsurveyor.apiservice.RetrofitClient
import com.gis.landsurveyor.responseModel.EmployeeInfo
import com.gis.landsurveyor.responseModel.RequestModel
import kotlinx.android.synthetic.main.fragment_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * A simple [Fragment] subclass.
 */
class ListFragment : Fragment() {
    lateinit var empInfo: EmployeeInfo
    lateinit var fContext: Context
    private val retrofitClient: RetrofitClient = RetrofitClient()
    private val apiService = retrofitClient.callApi()

    private val header : MutableList<String> = ArrayList()
    val body : MutableList<MutableList<RequestModel>> = ArrayList()
    private val tmpBody : MutableList<RequestModel> = ArrayList()


    companion object {
        lateinit var navController: NavController
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        val tmpHeaders =  resources.getStringArray(R.array.statusList)
        if(header.isEmpty()){tmpHeaders.forEach { header.add(it) }}
        Log.d("chikk", "header $header")
        empInfo = SingletonEmp.instance!!
        requestUser()
        callRequests(empInfo.employee_id)
    }

    fun requestUser(){
        callApi().getUser().enqueue(object : Callback<ArrayList<User>>{
            override fun onFailure(call: Call<ArrayList<User>>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<ArrayList<User>>,
                response: Response<ArrayList<User>>
            ) {
                d("chikk","result = ${response.body()?.get(0)}")
            }

        })
    }

    private fun callRequests(emp_id: Int?) {
        Log.d("chikk", "start again")
        apiService.getRequests(emp_id).enqueue(object : Callback<MutableList<RequestModel>> {
            override fun onFailure(call: Call<MutableList<RequestModel>>, t: Throwable) {
                Log.d("chikk", "${getString(R.string.error_request_list)}: $t")
            }
            override fun onResponse(call: Call<MutableList<RequestModel>>, response: Response<MutableList<RequestModel>>) {
                if (response.body()?.size == 0) {
                    Log.d("chikk", getString(R.string.empty_request_list))
                }else{
                    d("chikk","result = ${response.body()?.get(0)}")
                    val list = response.body()!!.groupBy { it.status }

                    if (body.isEmpty()){makBodyTemp()}
                    for (i in 0 until list.size){
                        Log.d("chikk", "round $i")
                        if (list.keys.elementAt(i)==header[0]){
                            body[0] = list[list.keys.elementAt(i)] as MutableList<RequestModel>
                        }
                        if (list.keys.elementAt(i)==header[1]){
                            body[1] = list[list.keys.elementAt(i)] as MutableList<RequestModel>
                        }
                        if (list.keys.elementAt(i)==header[2]){
                            body[2] = list[list.keys.elementAt(i)] as MutableList<RequestModel>
                        }
                        Log.d("chikk", "Body $body")
                    }
                    expandableListView.setAdapter(MyExpandableListAdapter(fContext,expandableListView,header,body))
                }
            }
        })
    }

    private fun callApi(): ApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(ApiService::class.java)
    }
    private fun makBodyTemp(){
        for (i in 0 until  header.size) {
            body.add(tmpBody)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fContext = context
    }

}
