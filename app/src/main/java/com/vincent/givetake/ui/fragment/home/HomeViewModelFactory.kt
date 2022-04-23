package com.vincent.givetake.ui.fragment.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vincent.givetake.data.repository.items.ItemsRepository
import com.vincent.givetake.di.Injection
import com.vincent.givetake.preference.UserPreferences

class HomeViewModelFactory(private val repository: ItemsRepository, private val userPreferences: UserPreferences) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(repository, userPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: HomeViewModelFactory? = null

        fun getInstance(pref: UserPreferences): HomeViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: HomeViewModelFactory(Injection.provideItemsRepository(), pref)
            }.also { instance = it }
    }
}