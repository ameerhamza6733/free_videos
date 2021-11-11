package com.rid.videosapp.dataClasses.pixelVideo.request

import retrofit2.http.Query

data class VideoPixelRequest(@Query("query")  var query: String ="",
                        @Query("page") var page: Int=0,
                        @Query("per_page" )var per_page: Int=0) {
}