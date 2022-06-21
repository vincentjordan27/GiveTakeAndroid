package com.vincent.givetake.ui.activity.resetpass

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vincent.givetake.data.repository.email.EmailRepository
import com.vincent.givetake.data.repository.users.UsersRepository
import com.vincent.givetake.data.source.request.ResetRequest
import com.vincent.givetake.data.source.request.UpdatePassRequest
import com.vincent.givetake.data.source.response.items.StatusResponse
import com.vincent.givetake.utils.Result
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ResetPassViewModel(private val emailRepository: EmailRepository, private val usersRepository: UsersRepository) : ViewModel() {
    val resetPassResult = MutableLiveData<Result<StatusResponse?>>()
    val updatePassResult = MutableLiveData<Result<StatusResponse?>>()

    fun resetPass(username: String, body: ResetRequest) = viewModelScope.launch {
        emailRepository.resetPass(username, body).collect {
            resetPassResult.value = it
        }
    }

    fun updatePass(username: String, body: UpdatePassRequest) = viewModelScope.launch {
        usersRepository.updatePassword(username, body).collect {
            updatePassResult.value = it
        }
    }

}