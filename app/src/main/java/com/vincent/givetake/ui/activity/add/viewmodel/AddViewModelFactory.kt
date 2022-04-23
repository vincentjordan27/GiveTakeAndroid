package com.vincent.givetake.ui.activity.add.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vincent.givetake.data.repository.items.ItemsRepository
import com.vincent.givetake.di.Injection

class AddViewModelFactory(private val itemsRepository: ItemsRepository): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddViewModel::class.java)) {
            return AddViewModel(itemsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: AddViewModelFactory? = null

        fun getInstance(): AddViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: AddViewModelFactory(Injection.provideItemsRepository())
            }.also { instance = it }
    }
}