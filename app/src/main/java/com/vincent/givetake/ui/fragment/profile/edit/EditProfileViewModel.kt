package com.vincent.givetake.ui.fragment.profile.edit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vincent.givetake.data.repository.users.UsersRepository
import com.vincent.givetake.data.source.request.UpdateProfileRequest
import com.vincent.givetake.data.source.response.items.AddItemImageResponse
import com.vincent.givetake.data.source.response.users.UpdateUserResponse
import com.vincent.givetake.data.source.response.users.UploadProfileImageResponse
import com.vincent.givetake.utils.Result
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File

class EditProfileViewModel(private val repository: UsersRepository) : ViewModel() {
    var imageUploadResult = MutableLiveData<Result<UploadProfileImageResponse?>>()
    var updateProfileResult = MutableLiveData<Result<UpdateUserResponse?>>()


    fun updateProfile(token: String, body: UpdateProfileRequest) = viewModelScope.launch {
        repository.updateProfile(token, body).collect {
            updateProfileResult.value = it
        }
    }

    fun uploadImageUser(file: File) = viewModelScope.launch {
        repository.uploadImageProfile(file).collect {
            imageUploadResult.value = it
        }
    }
}