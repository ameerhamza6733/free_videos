package com.rid.videosapp.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.gson.Gson
import com.rid.videosapp.BuildConfig
import com.rid.videosapp.R
import com.rid.videosapp.constants.Constants
import com.rid.videosapp.dataClasses.Search
import com.rid.videosapp.dataClasses.TopCategories
import com.rid.videosapp.dataClasses.TopCategoriesArray
import com.rid.videosapp.dataClasses.Wallpapers
import com.rid.videosapp.repostroy.PixelVideoRepo
import com.rid.videosapp.repostroy.SearchRepo
import com.rid.videosapp.utils.CommonKeys
import dev.sagar.lifescience.utils.Event
import dev.sagar.lifescience.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SearchViewModel() : ViewModel() {
    init {
        getTopCategories()
    }
    private var page: Int = 1
    var isNewData = true
    var queryToSearch = ""
    val TAG = "SearchViewModel"
    private val pixelVideoRepo = PixelVideoRepo()

    private val _mutableCategorys: MutableLiveData<Event<Resource<List<TopCategories>>>> =
        MutableLiveData()
    val categoriesLiveData: LiveData<Event<Resource<List<TopCategories>>>> = _mutableCategorys

    private val _pixelVideoMutableData: MutableLiveData<Event<Resource<Wallpapers>>> =
        MutableLiveData()
    val pixelVideoSearchLiveData: MutableLiveData<Event<Resource<Wallpapers>>> =
        _pixelVideoMutableData

    private val _searchHistoryMutable: MutableLiveData<List<Search>> = MutableLiveData()
    val searchHistoryLiveData: LiveData<List<Search>> = _searchHistoryMutable


    fun getTopCategories() {

        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        if (BuildConfig.DEBUG){
            remoteConfig.setConfigSettingsAsync(configSettings)

        }
        remoteConfig.setDefaultsAsync(R.xml.remote_config_default_key)
        remoteConfig.fetchAndActivate().addOnSuccessListener {
            Log.d(TAG,"fetchAndActivate sucess")
        }
            .addOnCompleteListener {
                Log.d(TAG, "fetchAndActivate compalte")
                if (it.isSuccessful) {
                    val categoriesJson =
                        FirebaseRemoteConfig.getInstance().getString(CommonKeys.RC_TOP_CATEGORIES)
                            .replace("^\"|\"$", "")

                    val gson = Gson()
                    val array =
                        gson.fromJson<TopCategoriesArray>(
                            categoriesJson,
                            TopCategoriesArray::class.java
                        )

                    array.TopCategories
                    _mutableCategorys.postValue(Event(Resource.Success(array.TopCategories)))
                }
            }

    }

    fun getPixelVideos() {

        _pixelVideoMutableData.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.IO) {
            Log.d(TAG, "pixel videos is called ")
            val reponse = pixelVideoRepo.getData(queryToSearch, page, Constants.PAER_PAGE)
            _pixelVideoMutableData.postValue(reponse)

            this@SearchViewModel.page++

        }
    }

    fun searchHistoryLastItem(searchRepo: SearchRepo) {
        viewModelScope.launch(Dispatchers.IO) {
            val query: Search? = searchRepo.getLastSearchItem()
            if (query == null) {
                queryToSearch="4k wallpaper"
                getPixelVideos()
            } else {
                queryToSearch=query?.query.toString()
                getPixelVideos()
            }
        }
    }

    fun searchHistory(searchRepo: SearchRepo) {
        viewModelScope.launch(Dispatchers.IO) {
            searchRepo.getSearchHistory().collect {

            }
        }
    }

    fun saveSearchQuery(searchRepo: SearchRepo) {
        viewModelScope.launch(Dispatchers.IO) {
            searchRepo.saveSearch(Search(queryToSearch, System.currentTimeMillis()))
        }
    }

}