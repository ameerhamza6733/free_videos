package com.rid.videosapp.utils

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import com.rid.videosapp.servieces.VideoWallpaper
import java.io.File

class DownloadUtils {

companion object {

    private val PROGRESS_DELAY = 1000L
    var handler: Handler = Handler()
    private var isProgressCheckerRunning = false

    var mVideoWallpaper = VideoWallpaper()
    val RootDirectoryFBShow =
        File(Environment.getDownloadCacheDirectory().toString() + "/Download/My Videos")
    val RootDirectoryFB = "/My Videos/"

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
                )
                { path, _uri ->
                }
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


    /**
     * Starts watching download progress.
     *
     * This method is safe to call multiple times. Starting an already running progress checker is a no-op.
     */
    private fun startProgressChecker() {
        if (!isProgressCheckerRunning) {
            progressChecker.run()
            isProgressCheckerRunning = true
        }
    }

    /**
     * Stops watching download progress.
     */
    private fun stopProgressChecker() {
        handler.removeCallbacks(progressChecker)
        isProgressCheckerRunning = false
    }

    /**
     * Checks download progress and updates status, then re-schedules itself.
     */
    private val progressChecker: Runnable = object : Runnable {
        override fun run() {
            try {

                // manager reference not found. Commenting the code for compilation
                //manager.refresh();
            } finally {
                handler.postDelayed(this, PROGRESS_DELAY)
            }
        }
    }
}
}