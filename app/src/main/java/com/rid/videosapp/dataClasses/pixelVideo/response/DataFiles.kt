package com.rid.videosapp.dataClasses.pixelVideo.response

import java.util.*
import kotlin.collections.ArrayList

data class DataFiles(
    val url: String, val image: String, val duration: Int, val user: UserDetails, val id: Int,
    val video_files: ArrayList<VideoDetail>,
    val video_pictures: ArrayList<VideoThumbs>
)
