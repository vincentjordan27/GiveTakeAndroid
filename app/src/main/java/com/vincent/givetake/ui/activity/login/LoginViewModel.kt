package com.vincent.givetake.ui.activity.login

import androidx.lifecycle.*
import com.vincent.givetake.data.repository.users.UsersRepository
import com.vincent.givetake.data.source.request.LoginRequest
import com.vincent.givetake.data.source.response.users.LoginResponse
import com.vincent.givetake.preference.UserPreferences
import com.vincent.givetake.utils.Result
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LoginViewModel(private val usersRepository: UsersRepository, private val pref: UserPreferences) : ViewModel() {
    var result = MutableLiveData<Result<LoginResponse?>?>()


    fun loginUser(data: LoginRequest) {
        result.value = null
        viewModelScope.launch {
            usersRepository.loginUser(data).collect {
                result.value = it
            }
        }
    }
    fun saveUserAccessKey(key: String) {
        viewModelScope.launch {
            pref.saveUserAccessKey(key)
        }
    }

    fun saveUserId(id: String) {
        viewModelScope.launch {
            pref.saveUserId(id)
        }
    }



}