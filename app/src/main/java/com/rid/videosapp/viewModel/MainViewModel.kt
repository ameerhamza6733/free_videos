package com.rid.videosapp.viewModel
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rid.videosapp.dataClasses.Video
import com.rid.videosapp.dataClasses.pixbay.PixabayMain
import com.rid.videosapp.dataClasses.pixbay.VideoDetails
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

    private val _pixaVideoMutableData:MutableLiveData<Event<Resource<ArrayList<Video>>>> = MutableLiveData()
    val pixaVideosSearchData : MutableLiveData<Event<Resource<ArrayList<Video>>>> = _pixaVideoMutableData

    fun getPixelVideos(query:String,page:Int,per_page:Int){
       viewModelScope.launch (Dispatchers.IO){
           Log.d(TAG,"pixel videos is called ")
          _pixelVideoMutableData.postValue( pixelVideoRepo.getData(query,page,per_page))
           Log.d(TAG,"pixel videos is called $_pixelVideoMutableData ")
       }
   }

    fun getPixaVideos(query: String){
      viewModelScope.launch(Dispatchers.IO) {
          _pixaVideoMutableData.postValue(pixelVideoRepo.getVidoesFromPixabay(query))
      }
    }
}