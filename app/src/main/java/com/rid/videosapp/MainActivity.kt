package com.rid.videosapp

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.work.*
import com.google.firebase.analytics.FirebaseAnalytics
import com.rid.videosapp.fragments.HomeFragment
import com.rid.videosapp.utils.CommonKeys
import com.rid.videosapp.workManager.NotificationWorker
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private var countDownTimer: CountDownTimer?=null
    private val TAG="MainActivityTAG"
    var onComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctxt: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val  mDownloadManager = ctxt?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val mostRecentDownload: Uri = mDownloadManager.getUriForDownloadedFile(id!!)
            Log.d(TAG,"mostRestct downlaod ${mostRecentDownload}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        startActivity()
        this.registerReceiver(onComplete,
            IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED)
        );


    }

    override fun onDestroy() {
        countDownTimer?.cancel()
        this.unregisterReceiver(onComplete)
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
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