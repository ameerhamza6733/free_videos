package com.rid.videosapp.utils

import android.widget.Toast
import androidx.fragment.app.Fragment
import dev.sagar.lifescience.utils.Resource
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun Fragment.toast(message: String) {
    Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
}

fun Throwable.isInternetError() = this is UnknownHostException || this is SocketTimeoutException
