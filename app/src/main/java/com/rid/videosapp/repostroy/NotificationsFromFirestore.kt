package com.rid.videosapp.repostroy

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.rid.videosapp.notification.CustomNotification
import com.rid.videosapp.utils.CommonKeys

class NotificationsFromFirestore {
    private val db = Firebase.firestore
    val TAG = "NotificationsFromFirestore"

    @SuppressLint("LongLogTag")
    fun getNotifications(context: Context) {
        db.collection(CommonKeys.FIRESTORE_COLLECTION)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG,"new notifaction from firebase")
                    val mydata = document.data
                    val tittle = mydata.getValue(CommonKeys.FB_TITTLE).toString()
                    val dec = mydata.getValue(CommonKeys.FB_DEC).toString()
                    val body = mydata.getValue(CommonKeys.FB_BODY).toString()
                    val imgUrl = mydata.getValue(CommonKeys.FB_IMG_URL).toString()
                    val vidUrl = mydata.getValue(CommonKeys.FB_VID_URL).toString()

                }
            }
            .addOnFailureListener {
                Log.d(TAG, "exception  ${it.message}")
            }
    }
}