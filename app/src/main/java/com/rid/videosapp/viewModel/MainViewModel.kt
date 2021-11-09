package com.rid.videosapp.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rid.videosapp.dataClasses.VideoMainClass
import com.rid.videosapp.network.VideoRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {
    val TAG = "MainViewModel"
    private var mutableLiveData:MutableLiveData<VideoMainClass>?=null

    fun getData(query:String,page:Int,per_page:Int):MutableLiveData<VideoMainClass>?{
        mutableLiveData= MutableLiveData()
        val request=VideoRequest.newInstance.getVidoes(query,page,per_page)
        request.enqueue(object :Callback<VideoMainClass>{
            override fun onResponse(
                call: Call<VideoMainClass>,
                response: Response<VideoMainClass>
            ) {
                val res=response.body()
                if (res!=null){
                    mutableLiveData!!.value=res
                }else{
                    Log.d(TAG,"view model not success")
                }

            }

            override fun onFailure(call: Call<VideoMainClass>, t: Throwable) {
                Log.d(TAG,"view model onFailure")
            }

        })

        return mutableLiveData!!
    }
}