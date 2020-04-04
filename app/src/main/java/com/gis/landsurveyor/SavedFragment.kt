package com.gis.landsurveyor

import android.os.Bundle
import android.util.Log.d
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import kotlin.properties.Delegates

/**
 * A simple [Fragment] subclass.
 */
class SavedFragment : Fragment() {
    private var area = 0.0
    private var deedID: Int = 0
    lateinit var deedNumber: String
    var deedState: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_saved, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        deedID = HomeActivity.currentRequest
        arguments?.let { arguments ->
//            area = SavedFragmentArgs.fromBundle(arguments).areaArgs.toDouble()
        }
        d("chikk","arearrrr dd = $area")
    }

}
