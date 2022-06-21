package com.vincent.givetake.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vincent.givetake.data.repository.email.EmailRepository
import com.vincent.givetake.data.repository.users.UsersRepository
import com.vincent.givetake.di.Injection
import com.vincent.givetake.ui.activity.resetpass.ResetPassViewModel
import com.vincent.givetake.ui.activity.resetphone.ResetPhoneViewModel

class UserEmailRepoViewModelFactory(private val emailRepository: EmailRepository, private val usersRepository: UsersRepository) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ResetPhoneViewModel::class.java)) {
            return ResetPhoneViewModel(emailRepository, usersRepository) as T
        } else if (modelClass.isAssignableFrom(ResetPassViewModel::class.java)) {
            return ResetPassViewModel(emailRepository, usersRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: UserEmailRepoViewModelFactory? = null

        fun getInstance(): UserEmailRepoViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: UserEmailRepoViewModelFactory(Injection.provideEmailRepository(), Injection.provideUsersRepository())
            }.also { instance = it }
    }
}