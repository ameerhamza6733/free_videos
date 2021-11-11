package com.rid.videosapp.network

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

private const val BASE_URL = "https://api.pexels.com/videos/"
const val API_KEY = "563492ad6f91700001000001581b67f38770410b96516ba0cf1d598b"

interface VideosInterface {
    @GET("search")
    suspend fun getVidoes(

        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") per_page: Int
    ): Response<VideoMainClass>

}

object VideoRequest {
   // val newInstance: VideosInterface

    private val okHttpClient=OkHttpClient.Builder()
        .addInterceptor {chain ->
            val original=chain.request()
            val requestBuilder=original.newBuilder()
                .addHeader("Authorization", API_KEY)
                .method(original.method, original.body)
            val request=requestBuilder.build()
            chain.proceed(request)
        }.build()



  val newInstance:VideosInterface by lazy {

      val retrofit=Retrofit.Builder()
          .baseUrl(BASE_URL)
          .addConverterFactory(GsonConverterFactory.create())
          .client(okHttpClient)
          .build()
      retrofit.create(VideosInterface::class.java)
  }
//            val retrofit = Retrofit.Builder()
//                .baseUrl(BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build()
//            newInstance = retrofit.create(VideosInterface::class.java)

    }



