package com.rid.videosapp.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rid.videosapp.constants.Constants
import com.rid.videosapp.dataClasses.Search
import com.rid.videosapp.dataClasses.Wallpapers
import com.rid.videosapp.repostroy.PixelVideoRepo
import com.rid.videosapp.repostroy.SearchRepo
import dev.sagar.lifescience.utils.Event
import dev.sagar.lifescience.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SearchViewModel() :ViewModel() {
    private var page: Int = 1
    var isNewData=true
    var queryToSearch = ""
    val TAG = "SearchViewModel"
    private val pixelVideoRepo = PixelVideoRepo()
    private val _pixelVideoMutableData: MutableLiveData<Event<Resource<Wallpapers>>> =
        MutableLiveData()
    val pixelVideoSearchLiveData: MutableLiveData<Event<Resource<Wallpapers>>> =
        _pixelVideoMutableData

    private val _searchHistoryMutable:MutableLiveData<List<Search>> = MutableLiveData()
    val searchHistoryLiveData:LiveData<List<Search>> = _searchHistoryMutable


    fun getPixelVideos(query: String) {
        this@SearchViewModel.queryToSearch=query
        _pixelVideoMutableData.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.IO) {
            Log.d(TAG, "pixel videos is called ")
            val reponse = pixelVideoRepo.getData(query, page, Constants.PAER_PAGE)
            _pixelVideoMutableData.postValue(reponse)
            Log.d(TAG, "pixel videos is called $_pixelVideoMutableData ")
            this@SearchViewModel.page++

        }
    }

    fun searchHistoryLastItem(searchRepo: SearchRepo){
        viewModelScope.launch (Dispatchers.IO){
           val query:Search?= searchRepo.getLastSearchItem()
            if (query==null){
                getPixelVideos("landscape")
            }else{
                getPixelVideos(query.query)
            }
        }
    }

    fun searchHistory(searchRepo: SearchRepo){
        viewModelScope.launch (Dispatchers.IO){
            searchRepo.getSearchHistory().collect {

            }
        }
    }

    fun saveSearchQuery(searchRepo: SearchRepo){
        viewModelScope.launch (Dispatchers.IO){
            searchRepo.saveSearch(Search(queryToSearch,System.currentTimeMillis()))
        }
    }

}