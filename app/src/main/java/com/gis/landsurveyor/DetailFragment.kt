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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.gis.landsurveyor.apiservice.RetrofitClient
import com.gis.landsurveyor.requestModel.UpdateRequest
import com.gis.landsurveyor.responseModel.RequestModel
import com.gis.landsurveyor.responseModel.UpdateResponse
import kotlinx.android.synthetic.main.detail_request.*
import kotlinx.android.synthetic.main.fragment_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates
import android.os.Build
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.denzcoskun.imageslider.models.SlideModel
import com.gis.landsurveyor.apiservice.BASE_URL
import com.gis.landsurveyor.responseModel.DeedType
import kotlinx.android.synthetic.main.edit_dialog.view.*
import kotlinx.android.synthetic.main.edit_dialog.view.close_symbol
import kotlinx.android.synthetic.main.save_dialog.view.*

/**
 * A simple [Fragment] subclass.
 */
class DetailFragment : Fragment() {
    private val apiService = RetrofitClient.callApi()
    var request: RequestModel? = null
    private var requestId by Delegates.notNull<Int>()
    lateinit var loadingDialog: AlertDialog
    var isShowImg: Boolean = false
    var spinnerDeedType: ArrayList<DeedType>? = null
    var deedState = 0
    lateinit var deedStateName:String
    private lateinit var deedNumber: String
    lateinit var editDialog:AlertDialog
    lateinit var confirmDialog:AlertDialog
    lateinit var dialogView:View

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

        HomeActivity.titleContainer.visibility = View.VISIBLE
        activeButton(false)
        image_slider.visibility = View.GONE
        editBtn.visibility = View.GONE
        requestId = HomeActivity.currentRequest
        navController = findNavController()

        callRequests()
        spinnerDeedType = SingletonConfig.instance?.resources?.deed_type

        routingBtn.setOnClickListener {
            if(request != null) {
                val intent = Intent(activity, NavigateActivity::class.java)
                intent.putExtra("latitude", request!!.latitude)
                intent.putExtra("longitude", request!!.longitude)
                startActivity(intent)
            }
        }

        acceptBtn.setOnClickListener {
            if(request != null){
                acceptAlert()
            }
        }

        startBtn.setOnClickListener {
            if(request != null) {
                navController.navigate(R.id.action_detailFragment_to_mapFragment)
            }
        }

        editBtn.setOnClickListener {
            if (spinnerDeedType != null){
                toEditDialog()
            }
        }


        viewImagesBtn.setOnClickListener {
            if (isShowImg) {
                image_slider.visibility = View.GONE
                arrow.setImageResource(R.drawable.arrow_down_img)
                isShowImg = false
            }else{
                if (request?.image_path != null) {
                    viewImagesBtn.setTextColor(ContextCompat.getColor(context!!, R.color.colorImageBtn))
                    viewImagesBtn.setBackgroundResource(R.drawable.selector_img)
                    imgSlideShow()
                    arrow.setImageResource(R.drawable.arrow_up_img)
                    isShowImg = true
                } else {
                    Toast.makeText(context, getString(R.string.image_not_found), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun callRequests() {
        showLoadDialog()
        apiService.getDetail(requestId).enqueue(object : Callback<RequestModel> {
            override fun onFailure(call: Call<RequestModel>, t: Throwable) {
                loadingDialog.dismiss()
                Toast.makeText(context, getString(R.string.error_request_list), Toast.LENGTH_SHORT).show()
            }
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<RequestModel>, response: Response<RequestModel>) {
                if(response.body() != null) {
                    request = response.body()!!
                    setUI()
                    setData()
                    activeButton(true)
                    loadingDialog.dismiss()
                }
            }
        })
    }



    private fun toEditDialog() {
        dialogView = LayoutInflater.from(context).inflate(R.layout.edit_dialog,null)
        val dialogBuilder = AlertDialog.Builder(context!!)
            .setView(dialogView)

        val type= ArrayList<String>()
        type.add(getString(R.string.editSpinner))
        spinnerDeedType!!.forEach {
            type.add(it.name)
        }
        if (dialogView.deedSpinner != null){
            val adapter = ArrayAdapter(context!!,R.layout.custom_dropdown,type)
            dialogView.deedSpinner.adapter = adapter
        }

        dialogView.deedSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                deedState = position
                deedStateName = type[position]
                if (position == 0){deedStateName = getString(R.string.blankText)}
            }
        }

        dialogView.areaText.text = request?.area.toString()
        dialogView.deedNum.setText(request?.deed_number.toString())

        if (request?.deed_status?.deed_type_id != null){
            dialogView.deedSpinner.setSelection(request?.deed_status?.deed_type_id!!)
        }

        editDialog = dialogBuilder.show()
        editDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogView.finishedBtn.setOnClickListener {
            deedNumber = dialogView.deedNum.text.toString()
            if ((deedNumber.isNotEmpty()) and (deedState != 0)) {
                toConfirmDialog(true)
            }else{
                Toast.makeText(context,getString(R.string.fill_info), Toast.LENGTH_SHORT).show()
            }
        }

        dialogView.saveBtn.setOnClickListener {
            deedNumber = if (dialogView.deedNum.text.toString().isBlank()){
                getString(R.string.blankText)
            }else{
                dialogView.deedNum.text.toString()
            }
            toConfirmDialog(false)
        }

        dialogView.close_symbol.setOnClickListener { editDialog.dismiss() }
    }

    private fun toConfirmDialog(isSubmit:Boolean) {
        val confirmDialogView = LayoutInflater.from(context).inflate(R.layout.save_dialog,null)
        val dialogBuilder = AlertDialog.Builder(context!!)
            .setView(confirmDialogView)
        confirmDialogView.dialogTitle.text = if (isSubmit){getString(R.string.submitDialog_title)}else{getString(R.string.saveDialog_title)}
        confirmDialogView.areaTxt.text = dialogView.areaText.text
        confirmDialogView.numTxt.text = deedNumber
        confirmDialogView.typeTxt.text = deedStateName


        confirmDialog = dialogBuilder.show()
        confirmDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        confirmDialogView.confirmBtn.setOnClickListener {

                if(isSubmit && deedNumber.isNotEmpty() && (deedState != 0)) {
                    finishRequest()
                }else if (!isSubmit){
                    saveRequest()
                }else{
                    Toast.makeText(context,getString(R.string.fill_info), Toast.LENGTH_SHORT).show()
                }

        }

        confirmDialogView.cancel_Btn.setOnClickListener {
            confirmDialog.dismiss()
        }
    }

    private fun finishRequest() {
        val updateData=
            UpdateRequest(deed_number = deedNumber,deed_type_id = deedState,status_id = 4)
        apiService.updateRequest(requestId,updateData).enqueue(object : Callback<UpdateResponse> {
            override fun onFailure(call: Call<UpdateResponse>, t: Throwable) {
                confirmDialog.dismiss()
                editDialog.dismiss()
                Toast.makeText(context,getString(R.string.cannot_submit), Toast.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<UpdateResponse>, response: Response<UpdateResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, getString(R.string.submit_success),Toast.LENGTH_SHORT).show()
                    callRequests()
                }
                confirmDialog.dismiss()
                editDialog.dismiss()
            }
        })
    }

    private fun saveRequest() {
        val updateData=
            UpdateRequest(deed_number = if (deedNumber == getString(R.string.blankText)){null}else{deedNumber},deed_type_id = if (deedState == 0){null}else{deedState})
        apiService.updateRequest(requestId,updateData).enqueue(object : Callback<UpdateResponse> {
            override fun onFailure(call: Call<UpdateResponse>, t: Throwable) {
                confirmDialog.dismiss()
                editDialog.dismiss()
                Toast.makeText(context,getString(R.string.cannot_save), Toast.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<UpdateResponse>, response: Response<UpdateResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, getString(R.string.save_success),Toast.LENGTH_SHORT).show()
                    callRequests()
                }
                confirmDialog.dismiss()
                editDialog.dismiss()
            }
        })
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ResourceAsColor")
    private fun setUI() {
        if (request?.image_path != null) {
            viewImagesBtn.setTextColor(ContextCompat.getColor(context!!, R.color.colorImageBtn))
            viewImagesBtn.setBackgroundResource(R.drawable.selector_img)
            arrow.setImageResource(R.drawable.arrow_down_img)
        } else {
            viewImagesBtn.setTextColor(ContextCompat.getColor(context!!, R.color.colorDetailTextInfo))
            viewImagesBtn.setBackgroundResource(R.drawable.noimg_detail_bg)
        }
        when (request!!.status_id) {
//            success
            4 -> {
                acceptBtn.visibility = View.GONE
                startBtn.visibility = View.GONE
                editBtn.visibility = View.GONE
                status_info.setTextColor(ContextCompat.getColor(context!!, R.color.colorSuccess))
                status_info.setBackgroundResource(R.drawable.success_tag)
            }
//            collecting
            3 -> {
                acceptBtn.visibility = View.GONE
                startBtn.visibility = View.VISIBLE

                if (request?.area!=null){
                    editBtn.visibility = View.VISIBLE
                }else{
                    editBtn.visibility = View.GONE
                }
                status_info.setTextColor(ContextCompat.getColor(context!!, R.color.colorCollecting))
                status_info.setBackgroundResource(R.drawable.collecting_tag)
            }
//            assigned
            else -> {
                acceptBtn.visibility = View.VISIBLE
                startBtn.visibility = View.GONE
                editBtn.visibility = View.GONE
                status_info.setTextColor(ContextCompat.getColor(context!!, R.color.colorAssigned))
                status_info.setBackgroundResource(R.drawable.assigned_tag)
            }
        }
    }

    private fun imgSlideShow() {
        val imageList = ArrayList<SlideModel>() // Create image list
        imageList.clear()
        for (i in 0 until (request?.image_path?.size!!)) {
            imageList.add(SlideModel(BASE_URL+request?.image_path?.get(i), true))
        }
        image_slider.setImageList(imageList)
        image_slider.stopSliding()
        image_slider.visibility = View.VISIBLE
    }


    private fun acceptAlert() {
        val builder = AlertDialog.Builder(context!!)
        val title = getString(R.string.warning_title)
        var message = getString(R.string.confirm_collecting)
        builder.setMessage(message)
        builder.setTitle(title)
        builder.setPositiveButton("Yes"){ _, _ ->
            acceptRequest()
        }
        builder.setNegativeButton("No"){ _, _ ->

        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun acceptRequest() {
        val updateData=UpdateRequest(status_id = 3)
        apiService.updateStatus(request!!.deed_id,updateData).enqueue(object : Callback<UpdateResponse> {
            override fun onFailure(call: Call<UpdateResponse>, t: Throwable) {
                Toast.makeText(context, getString(R.string.save_success),Toast.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<UpdateResponse>, response: Response<UpdateResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, getString(R.string.change_success),Toast.LENGTH_SHORT).show()
                    callRequests()
                }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun setData() {
        id_info.text = request?.deed_id.toString()
        dName_info.text = request?.deed_name
        cName_info.text = getString(R.string.cusName,request?.customer?.first_name , request?.customer?.last_name)
        phone_info.text = request?.customer?.phone_number
        address_info.text = request?.address
        date_info.text = request?.updated_date
        status_info.text = SingletonConfig.instance?.resources?.status?.find { it.status_id == request?.status_id }?.name
    }

    private fun activeButton(boolean: Boolean){
        acceptBtn.isActivated = boolean
        startBtn.isActivated = boolean
        routingBtn.isActivated = boolean
        viewImagesBtn.isActivated = boolean
    }

    private fun showLoadDialog() {
        loadingDialog = LoadDialog.showLoadDialog(context!!)
        loadingDialog.show()
    }

}
