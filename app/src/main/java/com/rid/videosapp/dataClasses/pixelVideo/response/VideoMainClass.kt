package com.rid.videosapp.dataClasses.pixelVideo.response

data class VideoMainClass(
    val total_results:Int,
    val prev_page:String,
    val next_page:String,
    val videos: ArrayList<DataFiles>
)
