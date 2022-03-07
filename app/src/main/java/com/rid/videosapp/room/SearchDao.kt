package com.app.myapplication.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

import com.rid.videosapp.dataClasses.Search
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchDao {
    @Insert(onConflict = REPLACE)
    fun save(search:Search)


    @Query("SELECT * FROM search")
    fun loadAllSchool():Flow<List<Search>>
@Query("SELECT * FROM search ORDER BY searchTime DESC LIMIT 1")
    fun getLastSearch():Search?


}