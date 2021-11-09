package com.rid.videosapp.network

import com.rid.videosapp.dataClasses.VideoMainClass
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

private const val BASE_URL = "https://api.pexels.com/videos/"
const val API_KEY = "563492ad6f91700001000001581b67f38770410b96516ba0cf1d598b"

interface VideosInterface {
    @Headers("Authorization: $API_KEY")
    @GET("search")
    fun getVidoes(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") per_page: Int
    ): Call<VideoMainClass>

}

object VideoRequest {
    val newInstance: VideosInterface

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        newInstance = retrofit.create(VideosInterface::class.java)
    }
}


