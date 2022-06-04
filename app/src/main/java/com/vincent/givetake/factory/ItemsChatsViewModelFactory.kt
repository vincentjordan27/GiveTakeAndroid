package com.vincent.givetake.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vincent.givetake.data.repository.chat.ChatRepository
import com.vincent.givetake.data.repository.items.ItemsRepository
import com.vincent.givetake.di.Injection
import com.vincent.givetake.ui.activity.detail.DetailViewModel
import com.vincent.givetake.ui.activity.items.add.viewmodel.AddViewModel
import com.vincent.givetake.ui.activity.receive.ReceiveViewModel
import com.vincent.givetake.ui.activity.receiver.ReceiverViewModel
import com.vincent.givetake.ui.activity.request.RequestViewModel

class ItemsChatsViewModelFactory(private val itemsRepository: ItemsRepository, private val chatRepository: ChatRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(itemsRepository, chatRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ItemsChatsViewModelFactory? = null

        fun getInstance(): ItemsChatsViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ItemsChatsViewModelFactory(Injection.provideItemsRepository() ,Injection.provideChatRepository())
            }.also { instance = it }
    }
}