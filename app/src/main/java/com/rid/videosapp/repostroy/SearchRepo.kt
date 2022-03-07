package com.rid.videosapp.repostroy

import com.app.myapplication.room.SearchDao
import com.rid.videosapp.dataClasses.Search
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchRepo (private val searchDao: SearchDao) {
    suspend fun saveSearch(search: Search){
        searchDao.save(search)
    }

    suspend fun getSearchHistory(): Flow<List<Search>> {
       return searchDao.loadAllSchool()
    }

    fun getLastSearchItem(): Search? {
return searchDao.getLastSearch()
    }
}