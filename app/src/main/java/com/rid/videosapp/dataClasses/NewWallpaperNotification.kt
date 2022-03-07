package com.rid.videosapp.dataClasses

import com.rid.videosapp.dataClasses.pixelVideo.response.DataFiles
import com.rid.videosapp.dataClasses.pixelVideo.response.VideoDetail
import com.rid.videosapp.dataClasses.pixelVideo.response.VideoMainClass
import com.rid.videosapp.dataClasses.pixelVideo.response.VideoThumbs

class NewWallpaperNotification(
    val title: String, val body: String, val image: String, var wallpaper: String,
    val wallpaperProvider: String = "",
    val video_files: ArrayList<VideoDetail>
)
