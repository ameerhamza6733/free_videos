package com.rid.videosapp.repostroy

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.rid.videosapp.constants.Constants
import com.rid.videosapp.dataClasses.Video
import com.rid.videosapp.dataClasses.Wallpapers
import com.rid.videosapp.dataClasses.pixbay.PixabayMain
import com.rid.videosapp.dataClasses.pixbay.VideoDetails
import com.rid.videosapp.dataClasses.pixelVideo.response.VideoDetail
import com.rid.videosapp.network.VideoRequest
import com.rid.videosapp.utils.Utils
import dev.sagar.lifescience.utils.Event
import dev.sagar.lifescience.utils.Resource
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PixelVideoRepo {
    val api = VideoRequest.newInstance
    val pixabay = ArrayList<Video>()
    val TAG = "PixelVideoRepo"
    var isNewData: Boolean = true
    suspend fun getData(
        query: String,
        page: Int,
        per_page: Int
    ): Event<Resource<Wallpapers>> {

        return try {
            val response = api.getVidoes(query, page, per_page,"portrait")
            return if (response.body() == null) {
                Event(Resource.Error(null, "body null"))
            } else {
                isNewData = false
                val videoMainClass = response.body()
                val listOfVideos=  arrayListOf<Video>()

                if (videoMainClass?.total_results==0){
                    return Event(Resource.Error(null,"No Wallpaper found for $query"))
                }
                for (i in videoMainClass?.videos!!.indices) {
                    var bestHight=0
                    var bestHightVideoUrl= videoMainClass.videos[i].video_files[0].link
                   videoMainClass.videos[i].video_files.forEach {

                       Log.d(TAG,"video file hight ${it.height}")
                       var hight= 0
                       it.height?.let { it2->
                           hight=it2.toInt()
                       }
                       if (hight<=Utils.getScreenHeight() && hight>bestHight){
                          bestHight=hight
                           bestHightVideoUrl=it.link
                       }
                   }
                    Log.d(TAG,"best hight will ${bestHight} ${Utils.getScreenHeight()}")

                    val abc = Video(
                        videoMainClass.videos[i].user.name,
                        videoMainClass.videos[i].image,
                       bestHightVideoUrl,
                        videoMainClass.videos[i].duration,
                        videoMainClass.videos[i].id,

                    )
                    listOfVideos.add(abc)


                }

              val wallpapers= Wallpapers(listOfVideos, videoMainClass.total_results,
                    videoMainClass.next_page,
                    videoMainClass.prev_page)

                Event(Resource.Success(wallpapers, ""))

            }
        } catch (e: Exception) {
            Log.d(TAG, "erors is ${e.message}")
            e.printStackTrace()
            Event(Resource.Error(e, e.message.toString()))

        }
    }


}