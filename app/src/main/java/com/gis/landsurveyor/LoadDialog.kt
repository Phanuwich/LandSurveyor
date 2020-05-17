package com.gis.landsurveyor

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog

class LoadDialog {
    companion object {

        lateinit var loadingDialog: AlertDialog

        fun showLoadDialog(context: Context): AlertDialog {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.loading_dialog, null)
            val dialogBuilder = AlertDialog.Builder(context)
            dialogBuilder.setView(dialogView)
            dialogBuilder.setCancelable(false)

            loadingDialog = dialogBuilder.create()
            loadingDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            return loadingDialog
        }
    }
}