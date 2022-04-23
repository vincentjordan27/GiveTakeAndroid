package com.vincent.givetake.ui.activity.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vincent.givetake.data.repository.users.UsersRepository
import com.vincent.givetake.data.source.request.RegisterRequest
import com.vincent.givetake.data.source.response.users.RegisterResponse
import com.vincent.givetake.utils.Result
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RegisterViewModel(private val usersRepository: UsersRepository) : ViewModel() {
    var result = MutableLiveData<Result<RegisterResponse?>?>()

    fun registerUser(data: RegisterRequest) {
        result.value = null
        viewModelScope.launch {
            usersRepository.registerUser(data).collect {
                result.value = it
            }
        }
    }
}