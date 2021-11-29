package com.rid.videosapp.repostroy

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.rid.videosapp.notification.CustomeNotification

class NotificationsFromFirestore {
   private val db = Firebase.firestore
    val TAG="NotificationsFromFirestore"
    @SuppressLint("LongLogTag")
     fun getNotifications(context: Context){
        db.collection("Notificaton")
            .get()
            .addOnSuccessListener {result->
                for (document in result) {
                    Log.d(TAG, "data is form firestore  ${document.id} => ${document.data}")
                  val mydata=document.data
                    val tittle=mydata.getValue("tittle").toString()
                    val dec=mydata.getValue("dec").toString()
                    val body=mydata.getValue("body").toString()
                    Log.d(TAG,"tittle is $tittle dec is $dec body is $body")

                    CustomeNotification.NotificationCall(context,tittle,body,dec)
                }
            }
            .addOnFailureListener {
                Log.d(TAG,"exception  ${it.message}")
            }
    }
}