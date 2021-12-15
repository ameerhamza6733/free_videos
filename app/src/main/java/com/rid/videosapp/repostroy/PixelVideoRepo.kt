package com.rid.videosapp.repostroy

import android.util.Log
import com.google.gson.Gson
import com.rid.videosapp.constants.Constants
import com.rid.videosapp.dataClasses.Video
import com.rid.videosapp.dataClasses.pixbay.PixabayMain
import com.rid.videosapp.dataClasses.pixbay.VideoDetails
import com.rid.videosapp.dataClasses.pixelVideo.response.VideoDetail
import com.rid.videosapp.network.VideoRequest
import dev.sagar.lifescience.utils.Event
import dev.sagar.lifescience.utils.Resource
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PixelVideoRepo {
    val api = VideoRequest.newInstance;
    val videoGen = ArrayList<Video>()
    val pixabay = ArrayList<Video>()
    val TAG = "PixelVideoRepo"
    var isNewData: Boolean = true
    suspend fun getData(
        query: String,
        page: Int,
        per_page: Int
    ): Event<Resource<ArrayList<Video>>> {
        videoGen.clear()
        return try {
            val response = api.getVidoes(query, page, per_page)
            return if (response.body() == null) {
                Event(Resource.Error(null, "body null"))
            } else {
                isNewData = false
                val videoMainClass = response.body()
                for (i in videoMainClass?.videos!!.indices) {
                    val abc = Video(
                        videoMainClass.videos[i].user.name,
                        videoMainClass.videos[i].image,
                        videoMainClass.videos[i].video_files[2].link,
                        videoMainClass.videos[i].duration,
                        videoMainClass.videos[i].id
                    )
                    videoGen.add(abc)
                    Log.d(TAG,"picture is ${videoMainClass.videos[0].video_pictures[0].picture}")

                }

                Event(Resource.Success(videoGen, ""))

            }
        } catch (e: Exception) {
            Log.d(TAG, "erors is ${e.message}")
            e.printStackTrace()
            Event(Resource.Error(e, e.message.toString()))

        }
    }

    suspend fun getVidoesFromPixabay(
        query: String,
        page: Int,
        per_page: Int
    ): Event<Resource<ArrayList<Video>>> {
       // pixabay.clear()
        return try {
            val myRecponse =
                api.getVideosFromPixabay(Constants.BASE_URL_PIXABAY, query, page, per_page)

            return if (myRecponse.body() == null) {
                Event(Resource.Error(null, "body null"))

            } else {
                val reponseJson = String(myRecponse.body()!!.bytes())
                val gson = Gson()
                val resToMianPixaby = gson.fromJson(reponseJson, PixabayMain::class.java)
                for (i in resToMianPixaby.hits.indices) {
                    val imgId = resToMianPixaby.hits[i].picture_id
                    val imgToSet = Constants.VIDEOURl + imgId + "_295x166.jpg"
                    val vidToSend = Video(
                        resToMianPixaby.hits[i].user,
                        imgToSet,
                        resToMianPixaby.hits[i].videos.small.url,
                        resToMianPixaby.hits[i].duration,
                        resToMianPixaby.hits[i].downloads
                    )
                    videoGen.add(vidToSend)
                }
                Log.d(TAG,"data from Pixa size is ${videoGen.size}  $resToMianPixaby")
                Event(Resource.Success(videoGen, ""))
            }
        } catch (e: Exception) {
            Log.d(TAG, "erors is ${e.message}")
            e.printStackTrace()
            Event(Resource.Error(e, e.message.toString()))

        }
    }
}