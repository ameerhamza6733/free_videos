package com.app.myapplication.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.rid.videosapp.dataClasses.Search

@Database(entities = arrayOf(Search::class), version = 1, exportSchema = false)

abstract class DataBaseModule: RoomDatabase() {

    abstract fun Dao(): SearchDao



    companion object{
        @Volatile
        private var INSTANCE: DataBaseModule? = null
    fun getDatabase(context: Context): DataBaseModule {
        // if the INSTANCE is not null, then return it,
        // if it is, then create the database
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                DataBaseModule::class.java,
                "search_database"
            ).build()
            INSTANCE = instance
            // return instance
            instance
        }
    }
}
}