package com.rid.videosapp.viewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rid.videosapp.dataClasses.Video
import com.rid.videosapp.dataClasses.pixelVideo.response.VideoMainClass
import com.rid.videosapp.repostroy.PixelVideoRepo
import dev.sagar.lifescience.utils.Event
import dev.sagar.lifescience.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
class MainViewModel : ViewModel() {
    val TAG = "MainViewModel"
    private val pixelVideoRepo = PixelVideoRepo()
    private val _pixelVideoMutableData:MutableLiveData<Event<Resource<ArrayList<Video>>>> = MutableLiveData()
    val pixelVideoSearchLiveData : MutableLiveData<Event<Resource<ArrayList<Video>>>> = _pixelVideoMutableData

    fun getPixelVideos(query:String,page:Int,per_page:Int){
       viewModelScope.launch (Dispatchers.IO){
          _pixelVideoMutableData.postValue( pixelVideoRepo.getData(query,page,per_page))
       }
   }

}