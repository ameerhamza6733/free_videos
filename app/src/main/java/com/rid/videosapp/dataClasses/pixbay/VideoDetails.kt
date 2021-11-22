package com.rid.videosapp.dataClasses.pixbay

data class VideoDetails(
    val duration: Int,
    val views: Int,
    val downloads: Int,
    val user: String,
    val userImageURL: String,
    val videos:VideoQualities
)
