package com.rid.videosapp.repostroy

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rid.videosapp.dataClasses.Video
import com.rid.videosapp.dataClasses.pixelVideo.response.DataFiles
import com.rid.videosapp.dataClasses.pixelVideo.response.VideoMainClass
import com.rid.videosapp.network.VideoRequest
import dev.sagar.lifescience.utils.Event
import dev.sagar.lifescience.utils.Resource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PixelVideoRepo {
    val api = VideoRequest.newInstance;
    val videoGen = ArrayList<Video>()
    suspend fun getData(
        query: String,
        page: Int,
        per_page: Int
    ): Event<Resource<ArrayList<Video>>> {

        return try {
            val response = api.getVidoes(query, page, per_page)
            return if (response.body() == null) {
                Event(Resource.Error(null, "body null"))


            } else {
                val videoMainClass = response.body()

                for (i in videoMainClass?.videos!!.indices) {
                    val abc = Video(
                        "",
                        videoMainClass.videos[i].image,
                        videoMainClass.videos[i].video_files[0].link,
                        videoMainClass.videos[i].duration
                    )
                    videoGen.add(abc)
                }

                Event(Resource.Success(videoGen, ""))

            }
        } catch (e: Exception) {
            e.printStackTrace()
            Event(Resource.Error(e, e.message.toString()))
        }
    }
}