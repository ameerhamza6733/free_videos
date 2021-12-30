package com.rid.videosapp.utils

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import java.io.File
import java.lang.Exception

class DownloadUtils {
    companion object{
        val RootDirectoryFBShow =
            File(Environment.getExternalStorageDirectory().toString() + "/Download/My Videos")
        const val RootDirectoryFB = "/My Videos/"
        @SuppressLint("ObsoleteSdkInt")
        fun downloadFile(
            downloadPath: String?,
            destinationPath: String,
            context: Context,
            FileName: String
        ) {
            val uri = Uri.parse(downloadPath) // Path where you want to download file.
            val request = DownloadManager.Request(uri)
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI) // Tell on which network you want to download file.
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) // This will show notification on top when downloading the file.
            request.setTitle(FileName + "") // Title for notification.
            request.setVisibleInDownloadsUi(true)
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                destinationPath + FileName
            ) // Storage directory path
            (context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager).enqueue(request) // This will start downloading
            try {
                if (Build.VERSION.SDK_INT >= 19) {
                    MediaScannerConnection.scanFile(
                        context, arrayOf(
                            File(
                                Environment.DIRECTORY_DOWNLOADS + "/"
                                        + destinationPath + FileName
                            ).absolutePath
                        ),
                        null
                    ) { path, uri -> }
                } else {
                    context.sendBroadcast(
                        Intent(
                            "android.intent.action.MEDIA_MOUNTED",
                            Uri.fromFile(File(Environment.DIRECTORY_DOWNLOADS + "/" + destinationPath + FileName))
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}