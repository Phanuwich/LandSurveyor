package com.gis.landsurveyor

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.gis.landsurveyor.apiservice.RetrofitClient
import com.gis.landsurveyor.responseModel.EmployeeInfo
import com.gis.landsurveyor.responseModel.RequestModel
import com.gis.landsurveyor.responseModel.RequestStatus
import kotlinx.android.synthetic.main.fragment_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 */
class ListFragment : Fragment() {
    lateinit var empInfo: EmployeeInfo
    private val apiService = RetrofitClient.callApi()

    private val header : MutableList<RequestStatus> = ArrayList()
    val body : MutableList<MutableList<RequestModel>> = ArrayList()
    private val tmpBody : MutableList<RequestModel> = ArrayList()


    companion object {
        lateinit var navController: NavController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Handle the back button event
                activity?.finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
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
        HomeActivity.titleContainer.visibility = View.VISIBLE
        navController = findNavController()
        empInfo = SingletonConfig.instance!!.user

        if(header.isEmpty()){SingletonConfig.instance!!.resources.status.forEach { header.add(it) }}

        callRequests(empInfo.employee_id)
        swipeRefreshLayout.setOnRefreshListener { callRequests(empInfo.employee_id) }
    }


    @SuppressLint("CheckResult")
    private fun callRequests(emp_id: Int?) {
//        var requestList : MutableList<MutableList<RequestModel>> = ArrayList()
//        val list = mutableListOf<Observable<MutableList<RequestModel>>>()
//        for (i in 2..4) {
//            list.add(RetrofitClient.callRequestService(emp_id ?: 0, i))
//        }
//        Observable.mergeDelayError(list)
//            .doOnComplete {
//                val i = requestList
//            }
//            .subscribe({
//                requestList.add(it)
//            },{
//                it
//            })

        apiService.getRequests(emp_id).enqueue(object : Callback<MutableList<RequestModel>> {
            override fun onFailure(call: Call<MutableList<RequestModel>>, t: Throwable) {
                Toast.makeText(context, getString(R.string.error_request_list), Toast.LENGTH_SHORT).show()
                swipeRefreshLayout.isRefreshing = false
            }
            override fun onResponse(call: Call<MutableList<RequestModel>>, response: Response<MutableList<RequestModel>>) {
                if (response.body()?.size == 0) {
                    swipeRefreshLayout.isRefreshing = false
                }else{
                    val list = response.body()!!.groupBy { it.status_id }
                    makBodyTemp()
                    for (i in 0 until header.size){
                        val itemTemp = if (list[list.keys.find { it == header[i].status_id }] == null){tmpBody}
                                                        else{list[list.keys.find { it == header[i].status_id }]}
                        body[i] = itemTemp as  MutableList<RequestModel>
                    }
                    expandableListView.setAdapter(MyExpandableListAdapter(context!!,expandableListView,header,body))
                    for (i in 0 until header.size) {
                        expandableListView.expandGroup(i)
                    }
                    swipeRefreshLayout.isRefreshing = false
                }
            }
        })
    }

    private fun makBodyTemp(){
        body.clear()
        for (i in 0 until  header.size) {
            body.add(tmpBody)
        }
    }
}
