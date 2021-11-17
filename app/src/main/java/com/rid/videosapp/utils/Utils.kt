package com.rid.videosapp.utils

import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.rid.videosapp.R

class Utils {
    companion object {
        fun showToast(context: Context, msg: String) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }

         fun openBoottomSheet(context: Context){
            val bottomView =
                LayoutInflater.from(context).inflate(R.layout.network_error_bottom_sheet, null)

            val bottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)

            bottomSheetDialog.setContentView(bottomView)
            bottomSheetDialog.show()
        }
    }
}