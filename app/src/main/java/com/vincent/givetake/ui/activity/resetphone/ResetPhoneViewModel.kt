package com.vincent.givetake.ui.activity.resetphone

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vincent.givetake.data.repository.email.EmailRepository
import com.vincent.givetake.data.repository.users.UsersRepository
import com.vincent.givetake.data.source.request.ResetRequest
import com.vincent.givetake.data.source.request.UpdatePhoneRequest
import com.vincent.givetake.data.source.response.items.StatusResponse
import com.vincent.givetake.utils.Result
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ResetPhoneViewModel(private val repository: EmailRepository, private val usersRepository: UsersRepository) : ViewModel() {
    val phoneResetOtp = MutableLiveData<Result<StatusResponse?>>()
    val updatePhoneResult = MutableLiveData<Result<StatusResponse?>>()

    fun sendResetOtp(accessKey: String, body: ResetRequest) = viewModelScope.launch {
        repository.resetPhone(accessKey, body).collect {
            phoneResetOtp.value = it
        }
    }

    fun updatePhone(accessKey: String, body: UpdatePhoneRequest) = viewModelScope.launch {
        usersRepository.updatePhone(accessKey, body).collect {
            updatePhoneResult.value = it
        }
    }

}