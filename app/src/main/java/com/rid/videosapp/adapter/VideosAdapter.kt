package com.rid.videosapp.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rid.videosapp.R
import com.rid.videosapp.dataClasses.Video
import com.rid.videosapp.fragments.HomeFragment
import com.rid.videosapp.fragments.HomeFragmentDirections
import com.rid.videosapp.fragments.PlayVideo
import android.os.Bundle




class VideosAdapter(
    val context: Context,
    val vidList: ArrayList<Video>,
    val fragment: HomeFragment,
) :
    RecyclerView.Adapter<VideosAdapter.MyViewHolder>() {
    val TAG = "VideosAdapter"

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgView = itemView.findViewById<ImageView>(R.id.rec_view_img_view_id)
        init {
            itemView.setOnClickListener {
                val args = Bundle()
                args.putString("myUrl",vidList[adapterPosition].videoUrl)
                args.putString("ownerName",vidList[adapterPosition].ownerName)
                args.putInt("duration",vidList[adapterPosition].vidDuration)
                args.putInt("vidId",vidList[adapterPosition].id)
                val playVideo=PlayVideo()
                playVideo.arguments=args
                val fm: FragmentManager = (context as AppCompatActivity).supportFragmentManager
                val ft: FragmentTransaction = fm.beginTransaction()
                ft.add(R.id.fragment_container,playVideo)
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
            .into(holder.imgView)
        Log.d(TAG,"videos are ${myList.videoImage}")
    }

    override fun getItemCount(): Int {
        return vidList.size
    }

}