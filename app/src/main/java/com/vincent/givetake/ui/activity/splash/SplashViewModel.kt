package com.vincent.givetake.ui.activity.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.vincent.givetake.preference.UserPreferences

class SplashViewModel(private val preferences: UserPreferences) : ViewModel() {
    fun getAccessKey() : LiveData<String> {
        return preferences.getUserAccessKey().asLiveData()
    }
}