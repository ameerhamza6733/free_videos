package com.rid.videosapp.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.rid.videosapp.MainActivity
import com.rid.videosapp.R
import com.rid.videosapp.constants.Constants
import java.util.*

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.rid.videosapp.dataClasses.NewWallpaperNotification
import com.rid.videosapp.utils.CommonKeys


class CustomNotification {
    companion object {
        val TAG = "CustomeNotification"

        @SuppressLint("ResourceAsColor")
        fun requestForNotification(
            context: Context,
            newWallpaperNotification: NewWallpaperNotification
        ) {
            Glide.with(context)
                .asBitmap()
                .load(newWallpaperNotification.image)
                .into(object : CustomTarget<Bitmap>() {

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }

                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        triggerNotificationToShow(context, newWallpaperNotification.title, newWallpaperNotification.body, resource, newWallpaperNotification.wallpaper)

                    }
                })
        }

        private fun triggerNotificationToShow(
            context: Context,
            tittle: String,
            body: String,
            imgBitmap: Bitmap,
            vidUrl: String
        ) {
            val notificationLayout = RemoteViews(context.packageName, R.layout.custome_notification)

            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra(
                CommonKeys.NOTIFICATION_URL,
                vidUrl
            )
            val pendingIntent = PendingIntent.getActivity(context, Constants.request, intent, 0)
            try {
                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val notificationID = Random().nextInt(Constants.BOUND)
                notificationManager.cancelAll()
                if (vidUrl.contains("jpeg") || vidUrl.contains("jpg") || vidUrl.contains("png")) {
                    notificationLayout.setViewVisibility(R.id.imageView3, View.INVISIBLE)
                }else{
                    notificationLayout.setViewVisibility(R.id.imageView3, View.VISIBLE)
                }
                notificationLayout.setTextViewText(R.id.tv_notify_tittle_two, tittle)
                notificationLayout.setTextViewText(R.id.tv_notify_body, body)
                notificationLayout.setImageViewBitmap(R.id.iv_notify_main_iv, imgBitmap)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    setupChannels(notificationManager)
                }
                val notificationSoundUri =
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val notificationBuilder = NotificationCompat.Builder(context, Constants.CAHNNEL_ID)
                    .setSmallIcon(R.drawable.videocam_24)
                    .setCustomContentView(notificationLayout)
                    .setCustomBigContentView(notificationLayout)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setContentIntent(pendingIntent)
                notificationManager.notify(notificationID, notificationBuilder.build())
            } catch (e: Exception) {
                Log.d(TAG, "notidcaton error is ${e.message}")
            }
        }


        @RequiresApi(Build.VERSION_CODES.O)
        fun setupChannels(notificationManager: NotificationManager?) {
            Log.d(TAG, "channel created")
            val adminChannel: NotificationChannel
            adminChannel =
                NotificationChannel(
                    Constants.CAHNNEL_ID,
                    Constants.NEWMATCH,
                    NotificationManager.IMPORTANCE_LOW
                )
            adminChannel.description = Constants.DEC
            adminChannel.enableLights(true)
            adminChannel.lightColor = Color.RED
            notificationManager?.createNotificationChannel(adminChannel)
        }


    }
}