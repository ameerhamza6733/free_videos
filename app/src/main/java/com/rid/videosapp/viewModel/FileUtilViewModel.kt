package com.rid.videosapp.viewModel

import android.app.Application
import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import android.os.Environment
import android.util.Log
import androidx.lifecycle.*
import com.rid.videosapp.utils.DownloadUtils
import dev.sagar.lifescience.utils.Event
import dev.sagar.lifescience.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class FileUtilViewModel(application: Application) :AndroidViewModel(application) {

    private val TAG = "FileUtilViewModel"

    private val _mutableDownloadProgress: MutableLiveData<Resource<Int>> = MutableLiveData()
    val liveDataReanmeFile: LiveData<Resource<Int>> = _mutableDownloadProgress



    fun downloadFile(url: String, ownerName: String) {
        val context = getApplication<Application>().applicationContext
        val donwloadId = DownloadUtils.downloadFile(
            url,
            context.filesDir,
            context,
            ownerName
        )

        viewModelScope.launch (Dispatchers.IO){
            var isDownlaoding=true
            while (isDownlaoding){
                val q = DownloadManager.Query()
                q.setFilterById(donwloadId)
                val downloadManger= (context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager)

                val cursor: Cursor = downloadManger.query(q)
                cursor.moveToFirst()
                val bytesDownloaded: Int = cursor.getInt(
                    cursor!!.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                )
                val bytesTotal: Int =
                    cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))

                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                    isDownlaoding = false
                    cursor.close()
                    _mutableDownloadProgress.postValue(Resource.Success(0))

                }else{
                    val  progress =  ((bytesDownloaded * 100L) / bytesTotal) ;
                  _mutableDownloadProgress.postValue(Resource.Loading(progress = progress.toInt()))
                }


            }
        }

    }
}


