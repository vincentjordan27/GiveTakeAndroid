package com.vincent.givetake.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vincent.givetake.data.repository.users.UsersRepository
import com.vincent.givetake.di.Injection
import com.vincent.givetake.preference.UserPreferences
import com.vincent.givetake.ui.activity.chat.ChatRoomViewModel
import com.vincent.givetake.ui.activity.login.LoginViewModel
import com.vincent.givetake.ui.fragment.profile.view.ProfileViewModel

class UsersPrefViewModelFactory(private val usersRepository: UsersRepository, private val pref: UserPreferences) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(usersRepository, pref) as T
        }else if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(usersRepository, pref) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: UsersPrefViewModelFactory? = null

        fun getInstance(pref: UserPreferences): UsersPrefViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: UsersPrefViewModelFactory(Injection.provideUsersRepository(), pref)
            }.also { instance = it }
    }
}