package com.rid.videosapp.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.rid.videosapp.R
import java.util.*

class MyNativeAds {
    companion object {

        var currentNativeAd: NativeAd? = null
        private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {

            adView.mediaView = adView.findViewById<MediaView>(R.id.ad_media)

            // Set other ad assets.
            adView.headlineView = adView.findViewById(R.id.ad_headline)
            adView.bodyView = adView.findViewById(R.id.ad_body)
            adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
            adView.iconView = adView.findViewById(R.id.ad_app_icon)
           // adView.priceView = adView.findViewById(R.id.ad_price)
            adView.starRatingView = adView.findViewById(R.id.ad_stars)
         //   adView.storeView = adView.findViewById(R.id.ad_store)
            adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

            (adView.headlineView as TextView).text = nativeAd.headline
            adView.mediaView.setMediaContent(nativeAd.mediaContent)

            if (nativeAd.body == null) {
                adView.bodyView.visibility = View.INVISIBLE
            } else {
                adView.bodyView.visibility = View.VISIBLE
                (adView.bodyView as TextView).text = nativeAd.body
            }

            if (nativeAd.callToAction == null) {
                adView.callToActionView.visibility = View.INVISIBLE
            } else {
                adView.callToActionView.visibility = View.VISIBLE
                (adView.callToActionView as Button).text = nativeAd.callToAction
            }

            if (nativeAd.icon == null) {
                adView.iconView.visibility = View.GONE
            } else {
                (adView.iconView as ImageView).setImageDrawable(
                    nativeAd.icon.drawable
                )
                adView.iconView.visibility = View.VISIBLE
            }
//            if (nativeAd.price == null) {
//                adView.priceView.visibility = View.INVISIBLE
//            } else {
//                adView.priceView.visibility = View.VISIBLE
//                (adView.priceView as TextView).text = nativeAd.price
//            }
//            if (nativeAd.store == null) {
//                adView.storeView.visibility = View.INVISIBLE
//            } else {
//                adView.storeView.visibility = View.VISIBLE
//                (adView.storeView as TextView).text = nativeAd.store
//            }
            if (nativeAd.starRating == null) {
                adView.starRatingView.visibility = View.INVISIBLE
            } else {
                (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
                adView.starRatingView.visibility = View.VISIBLE
            }
            adView.setNativeAd(nativeAd)
        }

        fun showNativeAds(context: Context, adView: NativeAdView, frameLayout: FrameLayout,adId:String) {
            val builder = AdLoader.Builder(context, adId)
            builder.forNativeAd { nativeAd ->
                currentNativeAd?.destroy()
                currentNativeAd = nativeAd
                populateNativeAdView(nativeAd, adView)
                frameLayout.addView(adView)
            }

            val adLoader = builder.withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                }
            }).build()

            adLoader.loadAd(AdRequest.Builder().build())
        }
    }
}