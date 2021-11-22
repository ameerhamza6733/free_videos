package com.rid.videosapp.dataClasses.pixbay

import com.rid.videosapp.dataClasses.EachVideoDetails

data class VideoQualities(
    val large: EachVideoDetails,
    val medium: EachVideoDetails,
    val small: EachVideoDetails,
    val tiny: EachVideoDetails
)
