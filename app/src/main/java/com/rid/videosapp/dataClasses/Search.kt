package com.rid.videosapp.dataClasses

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search")
data class Search(@PrimaryKey val query: String, val searchTime:Long) {
}