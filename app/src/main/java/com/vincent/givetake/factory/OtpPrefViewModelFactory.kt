package com.vincent.givetake.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vincent.givetake.data.repository.chat.ChatRepository
import com.vincent.givetake.data.repository.items.ItemsRepository
import com.vincent.givetake.data.repository.otp.OtpRepository
import com.vincent.givetake.data.repository.users.UsersRepository
import com.vincent.givetake.data.source.api.ChatService
import com.vincent.givetake.di.Injection
import com.vincent.givetake.preference.UserPreferences
import com.vincent.givetake.ui.activity.items.edit.EditViewModel
import com.vincent.givetake.ui.activity.otp.OtpViewModel
import com.vincent.givetake.ui.fragment.chat.ChatViewModel
import com.vincent.givetake.ui.fragment.home.HomeViewModel
import com.vincent.givetake.ui.fragment.items.ItemsViewModel

class OtpPrefViewModelFactory(private val repository: OtpRepository, private val usersRepository: UsersRepository, private val userPreferences: UserPreferences): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OtpViewModel::class.java)) {
            return OtpViewModel(repository, usersRepository, userPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: OtpPrefViewModelFactory? = null

        fun getInstance(pref: UserPreferences): OtpPrefViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: OtpPrefViewModelFactory(Injection.provideOtpRepository(), Injection.provideUsersRepository(), pref)
            }.also { instance = it }
    }

}