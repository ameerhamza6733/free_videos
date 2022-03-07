package com.rid.videosapp.workManager

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.rid.videosapp.sharedPref.PrefUtils
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.gson.Gson
import com.rid.videosapp.R
import com.rid.videosapp.dataClasses.NewWallpaperNotification
import com.rid.videosapp.notification.CustomNotification
import com.rid.videosapp.repostroy.NotificationsFromFirestore
import com.rid.videosapp.utils.CommonKeys
import com.rid.videosapp.utils.Utils

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

    fun callNotification(newWallpaperNotification: NewWallpaperNotification) {

        CustomNotification.requestForNotification(
            applicationContext,
           newWallpaperNotification
        )
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
                    Log.d(TAG,"new remote config ")
                    val oldKey= PrefUtils.getString(applicationContext,CommonKeys.RC_NEW_WALLPAPER_NOTIFICATION)
                    val isNew = remoteConfig.getString(CommonKeys.RC_NEW_WALLPAPER_NOTIFICATION)

                    if (false){
                        Log.d(TAG,"remote key $isNew")
                    }else{
                        val gson=Gson()
                        val newWallpaperNotification= gson.fromJson<NewWallpaperNotification>(isNew,NewWallpaperNotification::class.java)
                        if (newWallpaperNotification.wallpaperProvider.equals(CommonKeys.NEW_NOTIFICATION_WALLPAPER_PEXELS_PROVIDER)){

                            var bestHight=0
                            var bestHightVideoUrl= newWallpaperNotification.video_files[0].link
                            newWallpaperNotification.video_files.forEach {

                                Log.d(TAG,"video file hight ${it.height}")
                                var hight= 0
                                it.height?.let { it2->
                                    hight=it2.toInt()
                                }
                                if (hight<= Utils.getScreenHeight() && hight>bestHight){
                                    bestHight=hight
                                    bestHightVideoUrl=it.link
                                }

                            }
                            newWallpaperNotification.wallpaper=bestHightVideoUrl
                            PrefUtils.setString(applicationContext,CommonKeys.RC_NEW_WALLPAPER_NOTIFICATION,isNew)
                            callNotification(newWallpaperNotification)
                            val bundle = Bundle()
                            val firebaseAnalytics = FirebaseAnalytics.getInstance(applicationContext)
                            bundle.putString(CommonKeys.LOG_EVENT, "new notification against key $isNew")
                            firebaseAnalytics.logEvent(CommonKeys.NEW_NOTIFICAION, bundle)
                            Log.d(TAG,"remote key is else $isNew")
                        }
                    }

                } else {
                    Log.d(TAG, "Not successful")
                }

            }
    }
}