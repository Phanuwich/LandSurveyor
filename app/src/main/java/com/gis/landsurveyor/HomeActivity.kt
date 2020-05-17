package com.gis.landsurveyor

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log.d
import android.view.View
import android.widget.LinearLayout
import com.gis.landsurveyor.responseModel.RequestModel
import com.esri.arcgisruntime.geometry.Point
import kotlinx.android.synthetic.main.activity_home.*
import kotlin.properties.Delegates

class HomeActivity : AppCompatActivity() {

    val titleFragment = TitleFragment()
    companion object {
        lateinit var currentRequestModel: RequestModel
        lateinit var titleContainer: LinearLayout
        var currentRequest by Delegates.notNull<Int>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
//        supportActionBar!!.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        titleContainer =  findViewById(R.id.title_bar_fragment)
        supportFragmentManager.beginTransaction().replace(R.id.title_bar_fragment,titleFragment,titleFragment.tag).commit()
    }


    override fun onBackPressed() {
        super.onBackPressed()
//        val navFragment = supportFragmentManager.fragments.filter { it.tag == "NAV" }
//        if (!navFragment.isNullOrEmpty()) {
//            super.onBackPressed()
//        }
    }


}

