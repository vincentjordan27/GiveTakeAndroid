package com.vincent.givetake.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vincent.givetake.data.repository.users.UsersRepository
import com.vincent.givetake.di.Injection
import com.vincent.givetake.ui.activity.register.RegisterViewModel
import com.vincent.givetake.ui.fragment.profile.edit.EditProfileViewModel


class UsersRepositoryViewModelFactory(private val usersRepository: UsersRepository): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(usersRepository) as T
        }else if (modelClass.isAssignableFrom(EditProfileViewModel::class.java)) {
            return EditProfileViewModel(usersRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: UsersRepositoryViewModelFactory? = null

        fun getInstance(): UsersRepositoryViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: UsersRepositoryViewModelFactory(Injection.provideUsersRepository())
            }.also { instance = it }
    }
}