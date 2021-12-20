package com.rid.videosapp.adapter

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
import com.rid.videosapp.utils.CommonKeys

class VideosAdapter(
    val context: Context,
    val vidList: ArrayList<Video>,
    val fragment: HomeFragment
) :
    RecyclerView.Adapter<VideosAdapter.MyViewHolder>() {
    val TAG = "VideosAdapter"

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgView = itemView.findViewById<ImageView>(R.id.rec_view_img_view_id)
        init {
            itemView.setOnClickListener {
                val args = Bundle()
                args.putString(CommonKeys.VID_URL,vidList[adapterPosition].videoUrl)
                args.putString(CommonKeys.OWNER,vidList[adapterPosition].ownerName)
                args.putInt(CommonKeys.DURATION,vidList[adapterPosition].vidDuration)
                args.putInt(CommonKeys.VID_ID,vidList[adapterPosition].id)
                val playVideo=PlayVideo()
                playVideo.arguments=args
                val fm: FragmentManager = (context as AppCompatActivity).supportFragmentManager
                val ft: FragmentTransaction = fm.beginTransaction()
                ft.replace(R.id.fragment_container,playVideo)
                ft.addToBackStack(HomeFragment().TAG)
                ft.commit()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val myView = LayoutInflater.from(context).inflate(R.layout.each_recview_main, parent, false)
        return MyViewHolder(myView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val myList = vidList[position]

        Glide.with(context)
            .load(myList.videoImage)
            .placeholder(R.drawable.new_img_my)
            .into(holder.imgView)

    }

    override fun getItemCount(): Int {
        return vidList.size
    }



}