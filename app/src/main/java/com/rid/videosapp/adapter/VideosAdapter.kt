package com.rid.videosapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rid.videosapp.R
import com.rid.videosapp.dataClasses.Video
import com.rid.videosapp.fragments.HomeFragment
import com.rid.videosapp.fragments.PlayVideo
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.bumptech.glide.request.RequestOptions
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.firebase.analytics.FirebaseAnalytics
import com.rid.videosapp.utils.CommonKeys
import com.rid.videosapp.utils.MyNativeAds

class VideosAdapter(
    val context: Context,
    val vidList: ArrayList<Video>,
    val fragment: HomeFragment
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "VideosAdapter"
    private val CONTENT = 0
    private val AD = 1

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgView = itemView.findViewById<ImageView>(R.id.rec_view_img_view_id)

        init {
            itemView.setOnClickListener {
                val args = Bundle()
                val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
                args.putString(CommonKeys.LOG_EVENT, "video play clicked from homeFragment")
                firebaseAnalytics.logEvent(CommonKeys.VIDEOS_PLAY_CLICKED, args)
                args.putString(CommonKeys.VID_URL, vidList[adapterPosition].videoUrl)
                args.putString(CommonKeys.OWNER, vidList[adapterPosition].ownerName)
                args.putInt(CommonKeys.DURATION, vidList[adapterPosition].vidDuration)
                args.putInt(CommonKeys.VID_ID, vidList[adapterPosition].id)
                val playVideo = PlayVideo()
                playVideo.arguments = args
                val fm: FragmentManager = (context as AppCompatActivity).supportFragmentManager
                val ft: FragmentTransaction = fm.beginTransaction()
                ft.add(R.id.fragment_container, playVideo)
                ft.addToBackStack(HomeFragment().TAG)
                ft.commit()
            }
        }
    }

    inner class MyAdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {

           val fram=itemView.findViewById<FrameLayout>(R.id.ad_frame)
            val layoutAd =
                LayoutInflater.from(context).inflate(R.layout.ad_unified, null) as NativeAdView
            MyNativeAds.showNativeAds(context, layoutAd, fram,context.getString(R.string.admob_native_add))
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == CONTENT) {
            return MyViewHolder(
                LayoutInflater.from(context).inflate(R.layout.each_recview_main, parent, false)
            )
        }
//        val layout =
//            LayoutInflater.from(context)
//                .inflate(R.layout.native_frame_layout, null) as FrameLayout
//        val layoutAd =
//            LayoutInflater.from(context).inflate(R.layout.ad_unified, null) as NativeAdView
//        MyNativeAds.showNativeAds(context, layoutAd, layout)
        return MyAdViewHolder(
            LayoutInflater.from(context).inflate(R.layout.native_frame_layout, parent, false)
        )
    }

    override fun getItemViewType(position: Int): Int {
      return CONTENT
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
       when(holder) {
           is MyAdViewHolder -> {


           }
           is MyViewHolder -> {
               val list = vidList[holder.adapterPosition]
               holder as MyViewHolder
               Glide.with(context)
                   .applyDefaultRequestOptions(RequestOptions().placeholder(R.drawable.ic_baseline_image_24))
                   .load(list.videoImage)
                   .into(holder.imgView)
           }
       }

    }

    override fun getItemCount(): Int {
        return vidList.size
    }


}