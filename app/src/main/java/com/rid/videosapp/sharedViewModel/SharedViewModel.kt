package com.rid.videosapp.sharedViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel:ViewModel() {
    val message = MutableLiveData<Boolean>()

    fun sendMessage(retry: Boolean) {
        message.value = retry
    }
}