package com.vincent.givetake.ui.fragment.profile.view

import androidx.lifecycle.*
import com.vincent.givetake.data.repository.users.UsersRepository
import com.vincent.givetake.data.source.response.users.UserDataResponse
import com.vincent.givetake.preference.UserPreferences
import com.vincent.givetake.utils.Result
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ProfileViewModel(private val usersRepository: UsersRepository, private val pref: UserPreferences) : ViewModel() {
    val resultUserData = MutableLiveData<Result<UserDataResponse?>>()

    fun getUserData(token: String) {
        viewModelScope.launch {
            usersRepository.userData(token).collect {
                resultUserData.postValue(it)
            }
        }
    }

    fun getAccessKey() : LiveData<String> {
        return pref.getUserAccessKey().asLiveData()
    }
}