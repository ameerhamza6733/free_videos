package com.rid.videosapp.dataClasses.pixelVideo.response

data class DataFiles(val url:String,val image:String,val duration:Int,
    val video_files:ArrayList<VideoDetail>
)
