package com.rid.videosapp.utils

import android.content.Context
import android.widget.Toast

import android.content.Intent
import android.content.res.Resources
import android.database.Cursor
import android.net.Uri
import com.rid.videosapp.dataClasses.TopCategories
import android.provider.MediaStore
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson
import com.rid.videosapp.dataClasses.TopCategoriesArray
import org.json.JSONObject
import kotlin.math.log


class Utils {
    companion object {
        fun getScreenWidth(): Int {
            return Resources.getSystem().getDisplayMetrics().widthPixels
        }

        fun getScreenHeight(): Int {
            return Resources.getSystem().getDisplayMetrics().heightPixels
        }


        fun showToast(context: Context, msg: String) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
        fun getRealPathFromUri(context: Context, contentUri: Uri?): String? {
            var cursor: Cursor? = null
            return try {
                val proj = arrayOf(MediaStore.Video.Media.DATA)
                cursor = context.contentResolver.query(contentUri!!, proj, null, null, null)
                val column_index: Int =
                    cursor!!.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DATA)
                cursor!!.moveToFirst()
                cursor.getString(column_index)
            } finally {
                if (cursor != null) {
                    cursor.close()
                }
            }
        }


        fun shareImg(context: Context, url: String) {
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

        fun addTopCategories(): List<TopCategories> {

            val categoriesJson= FirebaseRemoteConfig.getInstance().getString(CommonKeys.RC_TOP_CATEGORIES).replace("^\"|\"$", "")

            val gson = Gson()
            val array= gson.fromJson<TopCategoriesArray>(categoriesJson,TopCategoriesArray::class.java)

            return array.TopCategories

        }

    }
}