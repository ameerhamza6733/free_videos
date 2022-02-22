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
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.google.android.gms.ads.MobileAds
import com.rid.videosapp.utils.Utils

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
        createTimer(COUNTER_TIME)
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
    private fun createTimer(seconds: Long) {
         countDownTimer = object : CountDownTimer(seconds * 1000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = millisUntilFinished / 1000 + 1
                // counterTextView.text = "App is done loading in: $secondsRemaining"
            }

            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                secondsRemaining = 0
                // counterTextView.text = "Done."

                val application = application as? MyApplication

                if (application == null) {
                    Log.e(LOG_TAG, "Failed to cast application to MyApplication.")

                    return
                }


                // Show the app open ad.
                application.showAdIfAvailable(
                    this@MainActivity,
                    object : MyApplication.OnShowAdCompleteListener {
                        override fun onShowAdComplete() {


                        }
                    })
            }
        }

    }
    private fun startActivity(){
        val notificationIntent = intent.getStringExtra(CommonKeys.NOTIFICATION_URL)
        if (notificationIntent != null) {
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


        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
//        val uploadWorkRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
//   .setConstraints(constraints)
//            .build()
        val saveRequest =
            PeriodicWorkRequestBuilder<NotificationWorker>(15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "NotificationWorker",
            ExistingPeriodicWorkPolicy.KEEP,
            saveRequest
        )
    }
}