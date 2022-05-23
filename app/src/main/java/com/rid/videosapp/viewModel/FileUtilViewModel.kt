package com.rid.videosapp.viewModel

import android.app.Application
import android.app.DownloadManager
import android.content.Context.DOWNLOAD_SERVICE
import android.os.Environment
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.*
import com.rid.videosapp.sharedPref.PrefUtils.setString
import com.rid.videosapp.utils.DownloadUtils
import dev.sagar.lifescience.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import kotlin.math.log


class FileUtilViewModel(application: Application) :AndroidViewModel(application) {

    private val TAG = "FileUtilViewModel"

    private val _mutableRenameFile: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    val liveDataReanmeFile: LiveData<Resource<Boolean>> = _mutableRenameFile

    fun renameFile(fromName: String, toName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val dir: File? =
                getApplication<Application>().applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            if (dir?.exists() == true) {
                val from = File(dir, fromName)
                val to = File(dir, toName)
                if (from.exists()) {
                    from.renameTo(to)
                    setString(
                        getApplication<Application>().applicationContext,
                        "currentWallpaper",
                        to.absolutePath
                    )
                    _mutableRenameFile.postValue(Resource.Success(true))
                } else {
                    _mutableRenameFile.postValue(Resource.Error(null, "File not founded"))
                }
            }
        }
    }

    fun downloadFile(url: String, ownerName: String) {
        val context = getApplication<Application>().applicationContext
        val donwloadId = DownloadUtils.downloadFile(
            url,
            context.filesDir,
            context,
            ownerName
        )


    }
}


