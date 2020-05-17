package com.gis.landsurveyor

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.get
import com.gis.landsurveyor.responseModel.RequestModel
import com.gis.landsurveyor.responseModel.RequestStatus
import kotlinx.android.synthetic.main.exandable_list_group.view.*

class MyExpandableListAdapter(var context :Context,var expandableListView: ExpandableListView,var header: MutableList<RequestStatus>, var body:MutableList<MutableList<RequestModel>>) : BaseExpandableListAdapter() {
    override fun getGroup(groupPosition: Int): RequestStatus {
        return header[groupPosition]
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ResourceAsColor", "StringFormatMatches", "ResourceType")
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
        val title = convertView?.findViewById<TextView>(R.id.listGroup)
        val tapGroup = convertView?.tapGroup
        val arrow = convertView?.findViewById<ImageView>(R.id.arrow)
        title?.text = context.getString(R.string.header_status,getGroup(groupPosition).name,getChildrenCount(groupPosition))
        when (groupPosition) {
            0 -> {
//                DrawableCompat.setTint(
//                    DrawableCompat.wrap(context.getDrawable(R.drawable.ic_label_btn1)!!),
//                    Color.parseColor("#FF5722")
//                )
                title?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_label_btn1,0,0,0)
            }
            1 -> {
                title?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_label_btn2,0,0,0)
            }
            else -> {
                title?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_label_btn3,0,0,0)
            }

        }

        tapGroup?.setOnClickListener {
            if(expandableListView.isGroupExpanded(groupPosition)) {
                arrow?.setImageResource(R.drawable.arrow_down)
                expandableListView.collapseGroup(groupPosition)
            }else {
                arrow?.setImageResource(R.drawable.arrow_up)
                expandableListView.expandGroup(groupPosition,true)
            }
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
        val tag = convertView?.findViewById<ImageView>(R.id.tag_item)

        title?.text = context.getString(R.string.item_id,getChild(groupPosition,childPosition).deed_id.toString())
        title2?.text = context.getString(R.string.item_name,getChild(groupPosition,childPosition).deed_name)
        when (groupPosition) {
            0 -> {
                tag?.setBackgroundResource(R.drawable.tag_item_collecting)
            }
            1 -> {
                tag?.setBackgroundResource(R.drawable.tag_item_assigned)
            }
            else -> {
                tag?.setBackgroundResource(R.drawable.tag_item_success)
            }
        }

        tap?.setOnClickListener {
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