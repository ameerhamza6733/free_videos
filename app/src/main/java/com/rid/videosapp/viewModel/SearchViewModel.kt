package com.rid.videosapp.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rid.videosapp.constants.Constants
import com.rid.videosapp.dataClasses.Video
import com.rid.videosapp.repostroy.PixelVideoRepo
import dev.sagar.lifescience.utils.Event
import dev.sagar.lifescience.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchViewModel(query: String) :ViewModel() {
    var page: Int = 1
    var isNewData=true
    var queryToSearch = ""
    val TAG = "SearchViewModel"
    private val pixelVideoRepo = PixelVideoRepo()
    private val _pixelVideoMutableData: MutableLiveData<Event<Resource<ArrayList<Video>>>> =
        MutableLiveData()
    val pixelVideoSearchLiveData: MutableLiveData<Event<Resource<ArrayList<Video>>>> =
        _pixelVideoMutableData

    init {

        getPixelVideos(query, page, Constants.PAER_PAGE)
        //  getPixaVideos(Constants.POPULAR_SEARCHES, page, Constants.PAER_PAGE)
        page++
    }



    fun getPixelVideos(query: String, page: Int, per_page: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d(TAG, "pixel videos is called ")
            _pixelVideoMutableData.postValue(pixelVideoRepo.getData(query, page, per_page))
            Log.d(TAG, "pixel videos is called $_pixelVideoMutableData ")

        }
    }


}