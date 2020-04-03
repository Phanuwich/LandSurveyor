package com.gis.landsurveyor

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Log.d
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.gis.landsurveyor.apiservice.RetrofitClient
import com.gis.landsurveyor.responseModel.Customer
import com.gis.landsurveyor.responseModel.RequestModel
import kotlinx.android.synthetic.main.detail_request.*
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.fragment_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates

/**
 * A simple [Fragment] subclass.
 */
class DetailFragment : Fragment() {

    private val retrofitClient: RetrofitClient = RetrofitClient()
    private val apiService = retrofitClient.callApi()
    lateinit var request: RequestModel
    var requestId by Delegates.notNull<Int>()

    companion object {
        lateinit var navController: NavController
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestId = HomeActivity.currentRequest
        Log.d("chikk", "start again = $requestId")
        navController = findNavController()


        callRequests()
        val startBtn = view.findViewById<Button>(R.id.startBtn)

        startBtn.setOnClickListener {
            navController.navigate(R.id.action_detailFragment_to_mapFragment)
        }
        routingBtn.setOnClickListener {
            val intent = Intent(context, NavigateActivity::class.java)
                    intent.putExtra("request",request)
            startActivity(intent)
        }

    }

    private fun callRequests() {
        Log.d("chikk", "start again")
        apiService.getDetail(requestId).enqueue(object : Callback<RequestModel> {
            override fun onFailure(call: Call<RequestModel>, t: Throwable) {
                Log.d("chikk", "${getString(R.string.error_request_list)}: $t")
            }
            override fun onResponse(call: Call<RequestModel>, response: Response<RequestModel>) {
                d("chikk","result = ${response.body()}")
                request = response.body()!!
                setData()

            }
        })
    }

//    private fun getCustomer() {
//        apiService.getCustomer(request.customer_id).enqueue(object : Callback<Customer>{
//            override fun onFailure(call: Call<Customer>, t: Throwable) {
//
//            }
//
//            override fun onResponse(call: Call<Customer>, response: Response<Customer>) {
//                if (response.isSuccessful){
//                    customer = response.body()!!
//                    setData()
//                }else{
//                    Toast.makeText(context, "error", Toast.LENGTH_LONG).show()
//                }
//            }
//
//        })
//    }

    @SuppressLint("SetTextI18n")
    private fun setData() {
        id_info.text = request.deed_id.toString()
        dName_info.text = request.deed_name
        cName_info.text = request.customer.first_name +" "+ request.customer.last_name
        phone_info.text = request.customer.phone_number
        address_info.text = request.address
        date_info.text = request.updated_date
        status_info.text = request.status
    }

}
