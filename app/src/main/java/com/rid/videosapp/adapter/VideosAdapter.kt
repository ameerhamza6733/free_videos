package com.rid.videosapp.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rid.videosapp.R
import com.rid.videosapp.dataClasses.DataFiles

class VideosAdapter(val context: Context, val vidList: ArrayList<DataFiles>) :
    RecyclerView.Adapter<VideosAdapter.MyViewHolder>() {
    val TAG="VideosAdapter"
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgView = itemView.findViewById<ImageView>(R.id.rec_view_img_view_id)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        Log.d(TAG,"onCreateViewHolder Call")
        val myView = LayoutInflater.from(context).inflate(R.layout.each_recview_main, parent, false)
        return MyViewHolder(myView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val myList = vidList[position]
        Log.d(TAG,"onBindCall")
        Log.d(TAG,"data size is ${vidList.size}")
        Glide.with(context)
            .load(myList.image)
            .into(holder.imgView)
    }

    override fun getItemCount(): Int {
        return vidList.size
    }
}