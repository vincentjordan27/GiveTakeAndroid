package com.vincent.givetake.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vincent.givetake.data.repository.items.ItemsRepository
import com.vincent.givetake.di.Injection
import com.vincent.givetake.ui.activity.add.viewmodel.AddViewModel
import com.vincent.givetake.ui.activity.detail.DetailViewModel

class ItemsRepositoryViewModelFactory(private val itemsRepository: ItemsRepository): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddViewModel::class.java)) {
            return AddViewModel(itemsRepository) as T
        }else if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(itemsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ItemsRepositoryViewModelFactory? = null

        fun getInstance(): ItemsRepositoryViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ItemsRepositoryViewModelFactory(Injection.provideItemsRepository())
            }.also { instance = it }
    }
}