package com.vincent.givetake.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vincent.givetake.data.repository.chat.ChatRepository
import com.vincent.givetake.data.repository.items.ItemsRepository
import com.vincent.givetake.data.source.api.ChatService
import com.vincent.givetake.di.Injection
import com.vincent.givetake.preference.UserPreferences
import com.vincent.givetake.ui.activity.items.edit.EditViewModel
import com.vincent.givetake.ui.fragment.chat.ChatViewModel
import com.vincent.givetake.ui.fragment.home.HomeViewModel
import com.vincent.givetake.ui.fragment.items.ItemsViewModel

class ChatPrefViewModelFactory(private val repository: ChatRepository, private val userPreferences: UserPreferences): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            return ChatViewModel(repository, userPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ChatPrefViewModelFactory? = null

        fun getInstance(pref: UserPreferences): ChatPrefViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ChatPrefViewModelFactory(Injection.provideChatRepository(), pref)
            }.also { instance = it }
    }

}