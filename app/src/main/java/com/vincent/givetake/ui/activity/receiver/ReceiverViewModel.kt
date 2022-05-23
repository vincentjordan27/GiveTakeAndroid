package com.vincent.givetake.ui.activity.receiver

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vincent.givetake.data.repository.items.ItemsRepository
import com.vincent.givetake.data.source.request.ChooseReceiverRequest
import com.vincent.givetake.data.source.response.items.ReceiverListResponse
import com.vincent.givetake.data.source.response.items.StatusResponse
import com.vincent.givetake.utils.Result
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ReceiverViewModel(private val repository: ItemsRepository) : ViewModel() {
    var receiverResult = MutableLiveData<Result<ReceiverListResponse?>>()
    var chooseReceiverResult = MutableLiveData<Result<StatusResponse?>>()

    fun getReceiverList(token: String, itemId: String) = viewModelScope.launch {
        repository.getReceiverList(token, itemId).collect {
            receiverResult.value = it
        }
    }

    fun chooseReceiver(token: String, itemId: String, body: ChooseReceiverRequest) = viewModelScope.launch {
        repository.chooseReceiver(token, itemId, body).collect {
            chooseReceiverResult.value = it
        }
    }
}