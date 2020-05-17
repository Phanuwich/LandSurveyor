package com.gis.landsurveyor

import android.os.Bundle
import android.util.Log
import android.util.Log.d
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.gis.landsurveyor.apiservice.RetrofitClient
import com.gis.landsurveyor.requestModel.UpdateRequest
import com.gis.landsurveyor.responseModel.DeedType
import com.gis.landsurveyor.responseModel.UpdateResponse
import kotlinx.android.synthetic.main.fragment_saved.*
import kotlinx.android.synthetic.main.save_dialog.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 */
class SavedFragment : Fragment() {
    private val apiService = RetrofitClient.callApi()
    private var area = 0.0
    private var deedID: Int = 0
    lateinit var deedNumber: String
    var deedState: Int? = 0
    var deedStateName: String? = null
    var spinnerDeedType: ArrayList<DeedType>? = null

    lateinit var navController: NavController
    lateinit var mAlertDialog:AlertDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        area = arguments?.getDouble("AREA")!!
        deedID = HomeActivity.currentRequest
        return inflater.inflate(R.layout.fragment_saved, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HomeActivity.titleContainer.visibility = View.VISIBLE

        navController = findNavController()
        spinnerDeedType = SingletonConfig.instance?.resources?.deed_type
        setSpinner()

        areaText.text = area.toString()


        saveBtn.setOnClickListener {
            deedNumber = if (deedNum.text.toString().isBlank()){
                getString(R.string.blankText)
            }else{
                deedNum.text.toString()
            }
            toResultDialog(false)
        }

        submitBtn.setOnClickListener {
            deedNumber = deedNum.text.toString()
            if (deedNumber.isNotEmpty() and (deedState != 0)) {
                toResultDialog(true)
            }else{
                Toast.makeText(context,getString(R.string.fill_info), Toast.LENGTH_SHORT).show()
            }
        }

        menuBtn.setOnClickListener { navController.navigate(R.id.action_savedFragment_to_listFragment) }

    }

    private fun toResultDialog(isSubmit:Boolean) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.save_dialog,null)
        val dialogBuilder = AlertDialog.Builder(context!!)
            .setView(dialogView)
        dialogView.dialogTitle.text = if (isSubmit){getString(R.string.submitDialog_title)}else{getString(R.string.saveDialog_title)}
        dialogView.areaTxt.text = area.toString()
        dialogView.numTxt.text = deedNumber
        dialogView.typeTxt.text = deedStateName


        mAlertDialog = dialogBuilder.show()
        mAlertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogView.confirmBtn.setOnClickListener {

            if(isSubmit && deedNumber.isNotEmpty() && (deedState != 0)) {
                finishRequest()
            }else if (!isSubmit){
                saveRequest()
            }else{
                Toast.makeText(context,getString(R.string.fill_info), Toast.LENGTH_SHORT).show()
            }

        }

        dialogView.cancel_Btn.setOnClickListener {
            mAlertDialog.dismiss()
        }

    }

    private fun finishRequest() {
        val updateData=
            UpdateRequest(area = area,deed_number = deedNumber,deed_type_id = deedState,status_id = 4)
        apiService.updateRequest(deedID,updateData).enqueue(object : Callback<UpdateResponse> {
            override fun onFailure(call: Call<UpdateResponse>, t: Throwable) {
                mAlertDialog.dismiss()
                Toast.makeText(context,getString(R.string.cannot_submit), Toast.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<UpdateResponse>, response: Response<UpdateResponse>) {
                if (response.isSuccessful) {
                    mAlertDialog.dismiss()
                    Toast.makeText(context, getString(R.string.submit_success),Toast.LENGTH_SHORT).show()
                    navController.navigate(R.id.action_savedFragment_to_listFragment)
                }
            }
        })    }

    private fun saveRequest() {
        val updateData=
            UpdateRequest(area = area,deed_number = if (deedNumber == getString(R.string.blankText)){null}else{deedNumber},deed_type_id = if (deedState == 0){null}else{deedState})
        apiService.updateRequest(deedID,updateData).enqueue(object : Callback<UpdateResponse> {
            override fun onFailure(call: Call<UpdateResponse>, t: Throwable) {
                mAlertDialog.dismiss()
                Toast.makeText(context,getString(R.string.cannot_save), Toast.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<UpdateResponse>, response: Response<UpdateResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, getString(R.string.save_success),Toast.LENGTH_SHORT).show()
                    mAlertDialog.dismiss()
                    navController.navigate(R.id.action_savedFragment_to_listFragment)
                }
            }
        })
    }

    fun setSpinner(){
        val type= ArrayList<String>()
        type.clear()
        type.add(getString(R.string.editSpinner))
        spinnerDeedType!!.forEach {
            type.add(it.name)
        }
        if (deedSpinner != null){
            val adapter = ArrayAdapter(context!!,R.layout.custom_dropdown,type)
            deedSpinner.adapter = adapter
        }

        deedSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                deedState = position
                deedStateName = type[position]
                if (position == 0){deedStateName = getString(R.string.blankText)}
            }
        }
    }
}
