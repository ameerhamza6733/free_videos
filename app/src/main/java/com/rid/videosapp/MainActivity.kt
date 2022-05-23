package com.rid.videosapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.*
import com.rid.videosapp.fragments.HomeFragment
import com.rid.videosapp.repostroy.NotificationsFromFirestore
import com.rid.videosapp.utils.CommonKeys
import com.rid.videosapp.workManager.NotificationWorker
import java.util.concurrent.TimeUnit
import android.content.ComponentName

import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import android.os.Debug
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.rid.videosapp.utils.Utils
import java.util.*

class MainActivity : AppCompatActivity() {
    private var countDownTimer: CountDownTimer?=null

    private val TAG = "MainActivity"
    private var secondsRemaining: Long = 0L
    private val COUNTER_TIME = 3L
    private val LOG_TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

       window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        startActivity()

//        val notificationsFromFirestore=NotificationsFromFirestore()
//        notificationsFromFirestore.getNotifications(this)

    }

    override fun onDestroy() {
        countDownTimer?.cancel()
        super.onDestroy()
        window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    override fun onPause() {
        super.onPause()
        window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    private fun startActivity(){
        val notificationIntent = intent.getStringExtra(CommonKeys.NOTIFICATION_URL)
        if (notificationIntent != null) {

            val firebaseAnalytics = FirebaseAnalytics.getInstance(applicationContext)
            firebaseAnalytics.logEvent(CommonKeys.AN_NEW_NOTIFICATION_OPEN, null)
            val bundle = Bundle()
            bundle.putString(CommonKeys.VID_URL, notificationIntent)
            val home = HomeFragment()
            home.arguments = bundle
            supportFragmentManager
                .beginTransaction()
                .add((R.id.fragment_container), home)
                .commit()

        } else {
            supportFragmentManager
                .beginTransaction()
                .replace((R.id.fragment_container), HomeFragment())
                .commit()
        }




        val constraints = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresDeviceIdle(true)
                .build()
        } else {
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        }

        if (BuildConfig.DEBUG){
            val checkNotificationWorker =
                OneTimeWorkRequestBuilder<NotificationWorker>()
                    .build()

            WorkManager.getInstance(this).enqueue(checkNotificationWorker)

        }else{
            val checkNotificationWorker =
                PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.DAYS)
                    .setConstraints(constraints)
                    .build()

            WorkManager.getInstance(this).enqueueUniquePeriodicWork("checkNewWallpaper",ExistingPeriodicWorkPolicy.KEEP,checkNotificationWorker)

        }
            }
}