package com.vincent.givetake.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vincent.givetake.data.repository.items.ItemsRepository
import com.vincent.givetake.data.repository.users.UsersRepository
import com.vincent.givetake.di.Injection
import com.vincent.givetake.preference.UserPreferences
import com.vincent.givetake.ui.activity.advice.AdviceViewModel
import com.vincent.givetake.ui.fragment.home.HomeViewModel

class ItemUserPrefViewModelFactory(private val itemRepository: ItemsRepository, private val usersRepository: UsersRepository, private val userPreferences: UserPreferences) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(itemRepository, usersRepository, userPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ItemUserPrefViewModelFactory? = null

        fun getInstance(pref: UserPreferences): ItemUserPrefViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ItemUserPrefViewModelFactory(Injection.provideItemsRepository(), Injection.provideUsersRepository(), pref)
            }.also { instance = it }
    }
}