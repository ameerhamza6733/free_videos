package com.rid.videosapp.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.analytics.FirebaseAnalytics
import com.rid.videosapp.R
import com.rid.videosapp.constants.Constants
import com.rid.videosapp.dataClasses.TopCategories
import com.rid.videosapp.fragments.HomeFragment
import com.rid.videosapp.fragments.SearchVideos
import com.rid.videosapp.utils.CommonKeys

class CategoriesAdapter(val context: Context, val list: List<TopCategories>) :
    RecyclerView.Adapter<CategoriesAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvRec: TextView = itemView.findViewById(R.id.textView2)
        val img: ImageView = itemView.findViewById(R.id.shapeableImageView2)
        init {
            view.setOnClickListener {
                val bundle = Bundle()
                val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
                bundle.putString(CommonKeys.LOG_EVENT, "video clicked from categories ${list[adapterPosition].ImgTittle}")
                firebaseAnalytics.logEvent(CommonKeys.VIDEOS_FROM_CATEGORIES, bundle)
                val fm: FragmentManager = (context as AppCompatActivity).supportFragmentManager
                bundle.putString(Constants.QUERY_KEY, list[adapterPosition].ImgQuery)
                val searchVideos = SearchVideos()
                searchVideos.arguments = bundle
                val ft: FragmentTransaction = fm.beginTransaction()
                ft.replace(R.id.fragment_container, searchVideos)
                ft.addToBackStack(HomeFragment().TAG)
                ft.commit()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val topCategoriesView = LayoutInflater.from(context)
            .inflate(R.layout.each_top_categories, parent, false)
        return MyViewHolder(topCategoriesView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val recList = list[position]
        holder.tvRec.text = recList.ImgTittle
        Glide.with(context)

            .load(recList.ImgeUrl)
            .into(holder.img)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}