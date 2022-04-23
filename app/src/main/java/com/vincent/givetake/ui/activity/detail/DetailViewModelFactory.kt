package com.vincent.givetake.ui.activity.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vincent.givetake.data.repository.items.ItemsRepository
import com.vincent.givetake.di.Injection
import com.vincent.givetake.preference.UserPreferences
import com.vincent.givetake.ui.activity.login.LoginViewModel
import com.vincent.givetake.ui.activity.login.LoginViewModelFactory

class DetailViewModelFactory(private val itemsRepository: ItemsRepository): ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(itemsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: DetailViewModelFactory? = null

        fun getInstance(): DetailViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: DetailViewModelFactory(Injection.provideItemsRepository())
            }.also { instance = it }
    }
}