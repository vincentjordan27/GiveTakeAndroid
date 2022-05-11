package com.vincent.givetake.ui.activity.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vincent.givetake.data.repository.users.UsersRepository
import com.vincent.givetake.data.source.request.RegisterRequest
import com.vincent.givetake.data.source.response.users.RegisterResponse
import com.vincent.givetake.data.source.response.users.UploadProfileImageResponse
import com.vincent.givetake.utils.Result
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File

class RegisterViewModel(private val usersRepository: UsersRepository) : ViewModel() {
    var result = MutableLiveData<Result<RegisterResponse?>>()
    var imageUploadResult = MutableLiveData<Result<UploadProfileImageResponse?>>()

    fun registerUser(data: RegisterRequest) {
        viewModelScope.launch {
            usersRepository.registerUser(data).collect {
                result.value = it
            }
        }
    }

    fun uploadImageUser(file: File) = viewModelScope.launch {
        usersRepository.uploadImageProfile(file).collect {
            imageUploadResult.value = it
        }
    }
}