package com.rid.videosapp.utils

import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.rid.videosapp.R
import androidx.core.content.ContextCompat.startActivity

import android.content.Intent
import androidx.core.content.ContextCompat


class Utils {
    companion object {
        fun showToast(context: Context, msg: String) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }

         fun shareImg(context: Context,url:String) {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_SUBJECT, "Android Studio Pro")
            intent.putExtra(
                Intent.EXTRA_TEXT,
                url
            )
            intent.type = "text/plain"
            context.startActivity(intent)
        }

    }
}