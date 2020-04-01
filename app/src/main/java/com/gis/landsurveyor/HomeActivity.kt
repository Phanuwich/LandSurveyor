package com.gis.landsurveyor

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gis.landsurveyor.responseModel.RequestModel

class HomeActivity : AppCompatActivity() {

    companion object {
        lateinit var currentRequestModel: RequestModel
        fun getActivity() :Activity{
            return getActivity()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
//        supportActionBar!!.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val titleFragment = TitleFragment()
        supportFragmentManager.beginTransaction().replace(R.id.title_bar_fragment,titleFragment,titleFragment.tag).commit()

    }

}
