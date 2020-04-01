package com.gis.landsurveyor

import android.os.Bundle
import android.util.Log.d
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        empInfo = SingletonEmp.instance!!
        val empName = getString(R.string.emp_name_label,empInfo.first_name , empInfo.last_name)
        emp_name_label.text = empName
        d("chikk","empName $empName")
    }
}
