package com.rid.videosapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rid.videosapp.fragments.HomeFragment
import com.rid.videosapp.repostroy.NotificationsFromFirestore
import com.rid.videosapp.utils.CommonKeys


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
//        val notificationsFromFirestore=NotificationsFromFirestore()
//        notificationsFromFirestore.getNotifications(this)
//        CustomeNotification.requestForNotification(
//            this,
//            "hello",
//            "hi",
//            "waseem asghar",
//            "https://images.pexels.com/photos/2014422/pexels-photo-2014422.jpeg?auto=compress&cs=tinysrgb&dpr=1&fit=crop&h=200&w=280",
//            "https://player.vimeo.com/external/435674472.hd.mp4?s=dec3d88a57aa801ff5c9bede151e48d21d16c46f&profile_id=174&oauth2_token_id=57447761"
//        )
    }
}