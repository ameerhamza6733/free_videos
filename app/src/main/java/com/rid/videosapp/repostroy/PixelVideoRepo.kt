package com.rid.videosapp.repostroy

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rid.videosapp.dataClasses.Video
import com.rid.videosapp.dataClasses.pixelVideo.response.VideoMainClass
import com.rid.videosapp.network.VideoRequest
import dev.sagar.lifescience.utils.Event
import dev.sagar.lifescience.utils.Resource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PixelVideoRepo  {
    val api= VideoRequest.newInstance;
    suspend fun getData(query:String,page:Int,per_page:Int):Event<Resource<VideoMainClass>>{

       return try {
           val  response=api .getVidoes(query,page,per_page)
           return if (response.body()==null) {
               Event(Resource.Error(null,"body null"))

           }else {
               val videoMainClass= response.body()

               Event(Resource.Success(videoMainClass!!,""))
           }
       }catch (e:Exception){
           e.printStackTrace()
           Event(Resource.Error(e,e.message.toString()))
       }
    }
}