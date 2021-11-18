package com.rid.videosapp.dialog

import android.app.Dialog
import android.content.Context
import android.widget.TextView
import com.rid.videosapp.R
import com.rid.videosapp.`interface`.CallBackInterface

class CustomDialog {
    companion object{
        fun showCustomDialog(
            context: Context,
            ownerName: String,
            callBack: CallBackInterface
        ) {
            val dialog = Dialog(context, R.style.ThemeOverlay_AppCompat_Dialog_Alert)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.custome_dialog)
            val tvDialogMsg = dialog.findViewById<TextView>(R.id.tv_owner_name_id)
            val yesBtn = dialog.findViewById<TextView>(R.id.btn_download)
            tvDialogMsg.text = ownerName
            yesBtn.setOnClickListener {
                callBack.onPositiveCallBack()
                dialog.dismiss()
            }
            dialog.setCancelable(true)
            dialog.create()
            dialog.show()
        }
    }
}