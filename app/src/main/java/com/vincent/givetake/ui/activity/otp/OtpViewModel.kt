package com.vincent.givetake.ui.activity.otp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vincent.givetake.data.repository.otp.OtpRepository
import com.vincent.givetake.data.repository.users.UsersRepository
import com.vincent.givetake.data.source.request.LoginRequest
import com.vincent.givetake.data.source.request.OtpRequest
import com.vincent.givetake.data.source.response.otp.OtpResponse
import com.vincent.givetake.data.source.response.users.LoginResponse
import com.vincent.givetake.preference.UserPreferences
import com.vincent.givetake.utils.Result
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class OtpViewModel(private val repository: OtpRepository, private val usersRepository: UsersRepository, private val userPreferences: UserPreferences) : ViewModel() {
    val otpSendResult = MutableLiveData<Result<OtpResponse?>>()
    var loginResult = MutableLiveData<Result<LoginResponse?>?>()


    fun loginUser(data: LoginRequest) {
        loginResult.value = null
        viewModelScope.launch {
            usersRepository.loginUser(data).collect {
                loginResult.value = it
            }
        }
    }

    fun sendOtp(body: OtpRequest) = viewModelScope.launch {
        repository.sendOtp(body).collect {
            otpSendResult.value = it
        }
    }

    fun saveUserAccessKey(key: String) {
        viewModelScope.launch {
            userPreferences.saveUserAccessKey(key)
        }
    }

    fun saveUserId(id: String) {
        viewModelScope.launch {
            userPreferences.saveUserId(id)
        }
    }


}