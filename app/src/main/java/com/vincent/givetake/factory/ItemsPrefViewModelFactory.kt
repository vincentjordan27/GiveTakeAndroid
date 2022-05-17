package com.vincent.givetake.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vincent.givetake.data.repository.items.ItemsRepository
import com.vincent.givetake.di.Injection
import com.vincent.givetake.preference.UserPreferences
import com.vincent.givetake.ui.activity.items.edit.EditViewModel
import com.vincent.givetake.ui.fragment.home.HomeViewModel

class ItemsPrefViewModelFactory(private val repository: ItemsRepository, private val userPreferences: UserPreferences) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(repository, userPreferences) as T
        } else if (modelClass.isAssignableFrom(EditViewModel::class.java)) {
            return EditViewModel(userPreferences, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ItemsPrefViewModelFactory? = null

        fun getInstance(pref: UserPreferences): ItemsPrefViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ItemsPrefViewModelFactory(Injection.provideItemsRepository(), pref)
            }.also { instance = it }
    }
}