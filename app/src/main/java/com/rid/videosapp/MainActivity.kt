package com.rid.videosapp

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


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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

        WorkManager.getInstance(this).enqueueUniquePeriodicWork("NotificationWorker",
            ExistingPeriodicWorkPolicy.KEEP,
            saveRequest)

    }
}