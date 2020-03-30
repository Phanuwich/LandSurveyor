package com.gis.landsurveyor

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
        title?.text = getGroup(groupPosition)
        if (groupPosition == 0){
            title?.setBackgroundResource(R.drawable.list_button_bg1)
        }else if (groupPosition == 1){
            title?.setBackgroundResource(R.drawable.list_button_bg2)
        }else{
            title?.setBackgroundResource(R.drawable.list_button_bg3)
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
    override fun getChild(groupPosition: Int, childPosition: Int): String {
        return body[groupPosition][childPosition].deed_name
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
        val title = convertView?.findViewById<TextView>(R.id.listText)
        val title2 = convertView?.findViewById<TextView>(R.id.listText2)
        title?.text = getChild(groupPosition,childPosition)
        title2?.text = "222222222222"
        title?.setOnClickListener {
            Toast.makeText(context, getChild(groupPosition,childPosition),Toast.LENGTH_SHORT).show()
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