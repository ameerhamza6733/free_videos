package com.rid.videosapp.workManager

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.task10driverapp.source.local.prefrance.PrefUtils
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.rid.videosapp.R
import com.rid.videosapp.notification.CustomNotification
import com.rid.videosapp.repostroy.NotificationsFromFirestore

class NotificationWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {
    private lateinit var remoteConfig: FirebaseRemoteConfig
    val TAG = "NotificationWorker"
    override fun doWork(): Result {
        try {

            checkForNewNotification()
            return Result.success()

        } catch (e: Exception) {
            Log.d(TAG, "catch is ${e.message} : Called")
            return Result.failure()
        }
    }

    fun callNotification() {
        val notificationsFromFirestore = NotificationsFromFirestore()
        notificationsFromFirestore.getNotifications(applicationContext)
    }


    private fun checkForNewNotification() {


        remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 60
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_default_key)

        remoteConfig.fetchAndActivate()
            .addOnCompleteListener {
                if (it.isSuccessful) {

                    val oldKey=PrefUtils.getString(applicationContext,"newnot")
                    val isNew = remoteConfig.getString("NotificationValue")

                    if (isNew==oldKey){
                        Log.d(TAG,"remote key $isNew")
                    }else{
                        PrefUtils.setString(applicationContext,"newnot",isNew)
                        callNotification()
                        Log.d(TAG,"remote key is else $isNew")
                    }

                } else {
                    Log.d(TAG, "Not successful")
                }

            }
    }
}