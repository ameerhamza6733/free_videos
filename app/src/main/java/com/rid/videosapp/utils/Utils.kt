package com.rid.videosapp.utils

import android.content.Context
import android.widget.Toast

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import com.rid.videosapp.dataClasses.TopCategories
import android.provider.MediaStore





class Utils {
    companion object {
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

        fun addTopCategories(): ArrayList<TopCategories> {

            val recList = ArrayList<TopCategories>()

            recList.add(
                TopCategories(
                    "Abstract ",
                    "abstract",
                    "https://images.pexels.com/photos/2179483/pexels-photo-2179483.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=80&w=140"
                )
            )
            recList.add(
                TopCategories(
                    "Painting",
                    "painting",
                    "https://images.pexels.com/photos/102127/pexels-photo-102127.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=80&w=140"

                )
            )
            recList.add(
                TopCategories(
                    "Fantasy ",
                    "fantasy ",
                    "https://images.pexels.com/photos/3025620/pexels-photo-3025620.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=80&w=140"
                )
            )
            recList.add(
                TopCategories(
                    "Retro",
                    "retro",
                    "https://images.pexels.com/photos/859895/pexels-photo-859895.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=80&w=140"
                )
            )
            recList.add(
                TopCategories(
                    "Planet",
                    "planet",
                    "https://images.pexels.com/photos/586030/pexels-photo-586030.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=80&w=140"
                )
            )
            return recList
        }

    }
}