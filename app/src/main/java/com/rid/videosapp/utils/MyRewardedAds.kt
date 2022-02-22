package com.rid.videosapp.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.rid.videosapp.constants.Constants

class MyRewardedAds() {

        val TAG = "MyRewardedAds"
         var mRewardedAd: RewardedAd? = null
         val AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"
        fun loadRewardedAd(context: Context) {
            Log.d(TAG,"add fun called")
            if (mRewardedAd == null) {
                val adRequest = AdRequest.Builder().build()
                RewardedAd.load(
                    context,AD_UNIT_ID, adRequest,
                    object : RewardedAdLoadCallback() {
                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            Log.d(TAG, adError.message)
                            mRewardedAd = null
                        }

                        override fun onAdLoaded(rewardedAd: RewardedAd) {
                            Log.d(TAG, "Ad was loaded.")
                            mRewardedAd = rewardedAd
                        }
                    }
                )
            }
        }

        fun setFullScreenContnt(activity: Activity) {

                mRewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        Log.d(TAG, "Ad was dismissed.")
                        mRewardedAd = null
                        loadRewardedAd(activity)
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                        Log.d(TAG, "Ad failed to show.")
                        mRewardedAd = null
                    }

                    override fun onAdShowedFullScreenContent() {
                        Log.d(TAG, "Ad showed fullscreen content.")
                        // Called when ad is dismissed.
                    }
                }



        }

}