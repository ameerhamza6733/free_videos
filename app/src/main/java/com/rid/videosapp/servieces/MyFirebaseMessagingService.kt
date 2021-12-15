package com.rid.videosapp.servieces

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.rid.videosapp.repostroy.NotificationsFromFirestore

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService : FirebaseMessagingService() {
    val TAG = "ServiceCalled"
    val context: Context = this
    override fun onMessageReceived(remoteData: RemoteMessage) {

        val myNotificatons = NotificationsFromFirestore()
        myNotificatons.getNotifications(context)

        Log.d(TAG, "onRecicved called")

        if (remoteData.data.isNotEmpty()) {
            Log.d(TAG, "Data rececived")
        }

        remoteData.notification?.let {
            Log.d(TAG, "data is ${it.body}")
        }
    }
}