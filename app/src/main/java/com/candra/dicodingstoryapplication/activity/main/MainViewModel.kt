package com.candra.dicodingstoryapplication.activity.main

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.candra.dicodingstoryapplication.data.repository.MainRepository
import com.candra.dicodingstoryapplication.model.StoryModel
import com.candra.dicodingstoryapplication.model.UserModel

class MainViewModel(private val mainRepository: MainRepository) : ViewModel() {
    private val _checkConnection = MutableLiveData<Boolean>()
    val checkConnection: LiveData<Boolean> = _checkConnection

    fun getSessionToken(): LiveData<UserModel> = mainRepository.getSessionToken().asLiveData()

    val story: LiveData<PagingData<StoryModel>> = mainRepository.getStory().cachedIn(viewModelScope)

    fun checkConnection(context: Context) {
        val connManager =
            context.getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val netCapabilities = connManager.getNetworkCapabilities(connManager.activeNetwork)
            _checkConnection.value = netCapabilities != null
        } else {
            val activeNetwork = connManager.activeNetworkInfo
            _checkConnection.value =
                activeNetwork?.isConnectedOrConnecting == true && activeNetwork.isAvailable
        }
    }
}