package com.gis.landsurveyor

import android.os.Bundle
import android.util.Log.d
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.gis.landsurveyor.responseModel.RequestModel
import kotlinx.android.synthetic.main.fragment_detail.*

/**
 * A simple [Fragment] subclass.
 */
class DetailFragment : Fragment() {

    lateinit var request: RequestModel

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
        request = HomeActivity.currentRequestModel
        d("chikk","ttt ?? = ${request.deed_name}")
        navController = findNavController()
        textView.text = request.deed_name
        val startBtn = view.findViewById<Button>(R.id.startBtn)

        startBtn.setOnClickListener {
            navController.navigate(R.id.action_detailFragment_to_mapFragment)
        }
        routingBtn.setOnClickListener {
            navController.navigate(R.id.navigationFragment)
        }
    }

}
