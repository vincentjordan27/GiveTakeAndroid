package com.vincent.givetake.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vincent.givetake.preference.UserPreferences
import com.vincent.givetake.ui.activity.splash.SplashViewModel


class PrefViewModelFactory( private val pref: UserPreferences) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            return SplashViewModel(pref) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: PrefViewModelFactory? = null

        fun getInstance(pref: UserPreferences): PrefViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: PrefViewModelFactory(pref)
            }.also { instance = it }
    }
}