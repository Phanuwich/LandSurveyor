package com.gis.landsurveyor

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.icu.text.LocaleDisplayNames
import android.text.Layout
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import com.gis.landsurveyor.responseModel.RequestModel

class MyExpandableListAdapter(var context :Context,var expandableListView: ExpandableListView,var header: MutableList<String>, var body:MutableList<MutableList<RequestModel>>) : BaseExpandableListAdapter() {
    override fun getGroup(groupPosition: Int): String {
        return header[groupPosition]
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    @SuppressLint("ResourceAsColor", "StringFormatMatches")
    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View? {
        var convertView = convertView
        if(convertView == null){
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.exandable_list_group,null)
        }
        val title = convertView?.findViewById<Button>(R.id.listGroup)
//        title?.text = getGroup(groupPosition)
        title?.text = context.getString(R.string.header_status,getGroup(groupPosition),getChildrenCount(groupPosition))
        when (groupPosition) {
            0 -> {
                title?.setBackgroundResource(R.drawable.list_button_bg1)
                title?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_label_btn1,0,0,0)
            }
            1 -> {
                title?.setBackgroundResource(R.drawable.list_button_bg2)
                title?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_label_btn2,0,0,0)
            }
            else -> {
                title?.setBackgroundResource(R.drawable.list_button_bg3)
                title?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_label_btn3,0,0,0)
            }
        }
        title?.setOnClickListener {
            if(expandableListView.isGroupExpanded(groupPosition))
                expandableListView.collapseGroup(groupPosition)
            else
                expandableListView.expandGroup(groupPosition)
            Toast.makeText(context, getGroup(groupPosition),Toast.LENGTH_SHORT).show()
        }
        return convertView
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return body[groupPosition].size
    }


    //edit from string to request type
    override fun getChild(groupPosition: Int, childPosition: Int): RequestModel {
        return body[groupPosition][childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View? {
        var convertView = convertView
        if(convertView == null){
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.expandable_list_item,null)
        }
        val tap = convertView?.findViewById<View>(R.id.tap_item)
        val title = convertView?.findViewById<TextView>(R.id.listText)
        val title2 = convertView?.findViewById<TextView>(R.id.listText2)

        title?.text = context.getString(R.string.item_id,getChild(groupPosition,childPosition).deed_id.toString())
        title2?.text = context.getString(R.string.item_name,getChild(groupPosition,childPosition).deed_name)

        tap?.setOnClickListener {
            //chane page here!
            Toast.makeText(context, getChild(groupPosition,childPosition).deed_name,Toast.LENGTH_SHORT).show()
            d("chikk","gfgfgfg = ${getChild(groupPosition,childPosition).deed_id}")
            HomeActivity.currentRequest = getChild(groupPosition,childPosition).deed_id
            HomeActivity.currentRequestModel = getChild(groupPosition,childPosition)
            ListFragment.navController.navigate(R.id.action_listFragment_to_detailFragment)
        }
        return convertView
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getGroupCount(): Int {
        return header.size
    }
}