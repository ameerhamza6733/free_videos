package com.rid.videosapp.network

import com.rid.videosapp.dataClasses.pixbay.PixabayMain
import com.rid.videosapp.dataClasses.pixelVideo.response.VideoMainClass
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import retrofit2.http.Url

import okhttp3.ResponseBody


private const val BASE_URL = "https://api.pexels.com/videos/"
private const val PIXA_BASE_URL = "https://pixabay.com/api/videos/"
const val API_KEY = "563492ad6f91700001000001581b67f38770410b96516ba0cf1d598b"
const val PAX_API_KEY = "24423765-31e6d0f91b182e2874f5079c0"

interface VideosInterface {
    @GET("search")
    suspend fun getVidoes(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") per_page: Int,
        @Query("orientation") orientation:String
    ): Response<VideoMainClass>

    @GET
    suspend fun getVideos(@Url url:String):Response<VideoMainClass>

    @GET
    suspend fun getVideosFromPixabay(
        @Url url: String?,
        @Query("q") query: String,
        @Query("page") page:Int,
        @Query("per_page")per_page:Int
    ): Response<ResponseBody>

}

object VideoRequest {

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .addHeader("Authorization", API_KEY)
                .method(original.method, original.body)
            val request = requestBuilder.build()
            chain.proceed(request)
        }.build()


    val newInstance: VideosInterface by lazy {

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
        retrofit.create(VideosInterface::class.java)
    }
}



