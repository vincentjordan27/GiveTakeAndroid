package com.vincent.givetake.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vincent.givetake.data.repository.chat.ChatRepository
import com.vincent.givetake.data.repository.items.ItemsRepository
import com.vincent.givetake.data.repository.users.UsersRepository
import com.vincent.givetake.di.Injection
import com.vincent.givetake.preference.UserPreferences
import com.vincent.givetake.ui.activity.chat.ChatRoomViewModel

class AllRepoViewModelFactory(private val chatRepository: ChatRepository, private val usersRepository: UsersRepository, private val repository: ItemsRepository, private val userPreferences: UserPreferences) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatRoomViewModel::class.java)) {
            return ChatRoomViewModel(chatRepository, usersRepository, repository, userPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: AllRepoViewModelFactory? = null

        fun getInstance(pref: UserPreferences): AllRepoViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: AllRepoViewModelFactory(Injection.provideChatRepository(), Injection.provideUsersRepository(), Injection.provideItemsRepository(), pref)
            }.also { instance = it }
    }
}