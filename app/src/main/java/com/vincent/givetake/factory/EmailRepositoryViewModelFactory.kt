package com.vincent.givetake.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vincent.givetake.data.repository.email.EmailRepository
import com.vincent.givetake.data.repository.users.UsersRepository
import com.vincent.givetake.di.Injection
import com.vincent.givetake.preference.UserPreferences
import com.vincent.givetake.ui.activity.resetphone.ResetPhoneViewModel

class EmailRepositoryViewModelFactory(private val emailRepository: EmailRepository, private val usersRepository: UsersRepository) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ResetPhoneViewModel::class.java)) {
            return ResetPhoneViewModel(emailRepository, usersRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: EmailRepositoryViewModelFactory? = null

        fun getInstance(): EmailRepositoryViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: EmailRepositoryViewModelFactory(Injection.provideEmailRepository(), Injection.provideUsersRepository())
            }.also { instance = it }
    }
}