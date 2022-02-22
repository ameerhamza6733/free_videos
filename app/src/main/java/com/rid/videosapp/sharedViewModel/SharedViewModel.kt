package com.rid.videosapp.sharedViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel:ViewModel() {
   val liveDataShowRewardedVideoAd:MutableLiveData<Boolean> = MutableLiveData();
}