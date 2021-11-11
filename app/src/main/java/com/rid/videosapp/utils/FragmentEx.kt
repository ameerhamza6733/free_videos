package com.rid.videosapp.utils

import android.widget.Toast
import androidx.fragment.app.Fragment

fun Fragment.toast(message:String){
    Toast.makeText(activity,message,Toast.LENGTH_LONG).show()
}