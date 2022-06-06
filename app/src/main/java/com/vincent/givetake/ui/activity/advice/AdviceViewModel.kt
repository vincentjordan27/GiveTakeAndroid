package com.vincent.givetake.ui.activity.advice

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vincent.givetake.data.repository.advice.AdviceRepository
import com.vincent.givetake.data.source.request.AddAdviceRequest
import com.vincent.givetake.data.source.response.advice.AddAdviceResponse
import com.vincent.givetake.data.source.response.advice.AllAdviceResponse
import com.vincent.givetake.utils.Result
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AdviceViewModel(private val repository: AdviceRepository): ViewModel() {

    val addAdviceResult = MutableLiveData<Result<AddAdviceResponse?>>()
    val myAdviceResult = MutableLiveData<Result<AllAdviceResponse?>>()

    fun addAdvice(token: String, body: AddAdviceRequest) = viewModelScope.launch {
        repository.addAdvice(token, body).collect {
            addAdviceResult.value = it
        }
    }

    fun myAdvices(token: String) = viewModelScope.launch {
        repository.myAdvices(token).collect {
            myAdviceResult.value = it
        }
    }
}