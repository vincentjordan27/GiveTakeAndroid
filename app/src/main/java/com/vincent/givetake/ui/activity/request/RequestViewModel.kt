package com.vincent.givetake.ui.activity.request

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vincent.givetake.data.repository.items.ItemsRepository
import com.vincent.givetake.data.source.request.ItemRequestBody
import com.vincent.givetake.data.source.response.items.StatusResponse
import com.vincent.givetake.utils.Result
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RequestViewModel(private val repository: ItemsRepository) : ViewModel() {
    var requestItemResult = MutableLiveData<Result<StatusResponse?>>()


    fun requestItem(token: String, itemId: String, body: ItemRequestBody) {
        viewModelScope.launch {
            repository.itemRequest(token, itemId, body).collect {
                requestItemResult.value = it
            }
        }
    }
}