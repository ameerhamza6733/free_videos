package com.rid.videosapp.viewModel

import android.app.Application
import android.os.Environment
import androidx.lifecycle.*
import com.rid.videosapp.sharedPref.PrefUtils.setString
import dev.sagar.lifescience.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class FileUtilViewModel(application: Application) :AndroidViewModel(application){
    private val _mutableRenameFile:MutableLiveData<Resource<Boolean>> = MutableLiveData()
    val liveDataReanmeFile:LiveData<Resource<Boolean>> = _mutableRenameFile

    fun renameFile(fromName:String,toName:String){
        viewModelScope.launch(Dispatchers.IO) {
            val dir: File? = getApplication<Application>().applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            if (dir?.exists()==true) {
                val from = File(dir, fromName)
                val to = File(dir, toName)
                if (from.exists()) {
                    from.renameTo(to)
                    setString(getApplication<Application>().applicationContext, "currentWallpaper", to.absolutePath)
                    _mutableRenameFile.postValue(Resource.Success(true))
                }else{
                    _mutableRenameFile.postValue(Resource.Error(null,"File not founded"))
                }
            }
        }
    }
}