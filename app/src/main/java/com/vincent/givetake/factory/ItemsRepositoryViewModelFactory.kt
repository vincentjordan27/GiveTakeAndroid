package com.vincent.givetake.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vincent.givetake.data.repository.items.ItemsRepository
import com.vincent.givetake.di.Injection
import com.vincent.givetake.ui.activity.items.add.viewmodel.AddViewModel
import com.vincent.givetake.ui.activity.detail.DetailViewModel
import com.vincent.givetake.ui.activity.receiver.ReceiverViewModel
import com.vincent.givetake.ui.activity.request.RequestViewModel

class ItemsRepositoryViewModelFactory(private val itemsRepository: ItemsRepository): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddViewModel::class.java)) {
            return AddViewModel(itemsRepository) as T
        }else if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(itemsRepository) as T
        }else if (modelClass.isAssignableFrom(RequestViewModel::class.java)) {
            return RequestViewModel(itemsRepository) as T
        }else if(modelClass.isAssignableFrom(ReceiverViewModel::class.java)) {
            return ReceiverViewModel(itemsRepository) as T
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