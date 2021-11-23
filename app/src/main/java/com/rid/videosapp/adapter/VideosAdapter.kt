package com.rid.videosapp.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rid.videosapp.R
import com.rid.videosapp.dataClasses.Video
import com.rid.videosapp.fragments.HomeFragment
import com.rid.videosapp.fragments.HomeFragmentDirections
import com.rid.videosapp.utils.Utils
import dev.sagar.lifescience.utils.Resource

class VideosAdapter(
    val context: Context,
    val vidList: ArrayList<Video>,
    val fragment: HomeFragment
) :
    RecyclerView.Adapter<VideosAdapter.MyViewHolder>() {
    val TAG = "VideosAdapter"

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgView = itemView.findViewById<ImageView>(R.id.rec_view_img_view_id)
        val morebtn = itemView.findViewById<TextView>(R.id.btn_more)

        init {
            morebtn.setOnClickListener {
                try {
                    fragment.requestForNewData()
                } catch (e: Exception) {
                    Log.d(TAG, "error calling vm ${e.message}")
                }

            }

            itemView.setOnClickListener {
                val action =
                    HomeFragmentDirections.actionHomeFragmentToPlayVideo(
                        vidList[adapterPosition].videoUrl,
                        vidList[absoluteAdapterPosition].ownerName,
                        vidList[absoluteAdapterPosition].vidDuration,
                        vidList[absoluteAdapterPosition].id
                    )

                it.findNavController().navigate(action)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        Log.d(TAG, "onCreateViewHolder Call")
        val myView = LayoutInflater.from(context).inflate(R.layout.each_recview_main, parent, false)
        return MyViewHolder(myView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val myList = vidList[position]
        Glide.with(context)
            .load(myList.videoImage)
            .into(holder.imgView)

        if (position==vidList.size-1){
            holder.morebtn.visibility=View.VISIBLE
        }else{
            holder.morebtn.visibility=View.INVISIBLE
        }
    }

    override fun getItemCount(): Int {
        return vidList.size
    }
}