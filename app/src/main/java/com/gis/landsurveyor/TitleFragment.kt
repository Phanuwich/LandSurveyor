package com.gis.landsurveyor

import android.content.Intent
import android.os.Bundle
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gis.landsurveyor.responseModel.EmployeeInfo
import kotlinx.android.synthetic.main.fragment_title.*


/**
 * A simple [Fragment] subclass.
 */
class TitleFragment : Fragment() {

    lateinit var empInfo: EmployeeInfo
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_title, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        empInfo = SingletonConfig.instance!!.user
        val empName = getString(R.string.emp_name_label,empInfo.first_name , empInfo.last_name)
        emp_name_label.text = empName

        logout_btn.setOnClickListener {
//            activity!!.supportFragmentManager.popBackStack()
            SingletonConfig.instance = null
            val intent = Intent(context, LoginActivity::class.java)
            activity?.finish()
            startActivity(intent)
        }
    }
}
