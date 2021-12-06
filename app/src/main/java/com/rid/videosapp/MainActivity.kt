package com.rid.videosapp

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.navigation.fragment.NavHostFragment
import com.rid.videosapp.constants.Constants
import com.rid.videosapp.fragments.HomeFragment
import com.rid.videosapp.notification.CustomeNotification
import com.rid.videosapp.repostroy.NotificationsFromFirestore
import java.util.*


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager
            .beginTransaction()
            .replace((R.id.fragment_container),HomeFragment())
            .commit()
//        val navHostFragment =
//            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
//        val navController = navHostFragment.navController
//        val myNotificatons = NotificationsFromFirestore()
//        myNotificatons.getNotifications(this)

//        CustomeNotification.NotificationCall(this, notificationLayout)
    }
}