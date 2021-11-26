package com.rid.videosapp.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.common.reflect.Reflection.getPackageName
import com.rid.videosapp.MainActivity
import com.rid.videosapp.R
import com.rid.videosapp.constants.Constants
import java.util.*

class CustomeNotification {
    companion object {
val TAG="CustomeNotification"
        fun NotificationCall(context: Context,remoteViews: RemoteViews){

         //   val img=remoteViews.setImageViewUri(R.id.iv_notify_main_iv, Uri.parse("${context.resources.getDrawable(R.drawable.new_img)}"))
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, Constants.request, intent, 0)
            try {
                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val notificationID = Random().nextInt(Constants.BOUND)
                Log.d(TAG,"Notiic obj created")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    setupChannels(notificationManager) }

                val notificationSoundUri =
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val notificationBuilder = NotificationCompat.Builder(context, Constants.CAHNNEL_ID)
                    .setSmallIcon(R.drawable.videocam_24)
                    .setCustomBigContentView(remoteViews)
                    .setSound(notificationSoundUri)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setContentIntent(pendingIntent)
                notificationManager.notify(notificationID, notificationBuilder.build())
            }catch (e:Exception){
                Log.d(TAG,"notidcaton error is ${e.message}")
            }

        }


        @RequiresApi(Build.VERSION_CODES.O)
        fun setupChannels(notificationManager: NotificationManager?) {
            Log.d(TAG,"channel created")
            val adminChannel: NotificationChannel
            adminChannel =
                NotificationChannel(
                    Constants.CAHNNEL_ID,
                    Constants.NEWMATCH,
                    NotificationManager.IMPORTANCE_HIGH
                )
            adminChannel.description = Constants.DEC
            adminChannel.enableLights(true)
            adminChannel.lightColor = Color.RED
            adminChannel.enableVibration(true)
            notificationManager?.createNotificationChannel(adminChannel)
        }


    }
}