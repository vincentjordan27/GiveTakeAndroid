package com.vincent.givetake.ui.activity.add.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vincent.givetake.data.repository.items.ItemsRepository
import com.vincent.givetake.data.source.request.AddItemRequest
import com.vincent.givetake.data.source.response.items.AddItemResponse
import com.vincent.givetake.data.source.response.items.GenerateIdResponse
import com.vincent.givetake.utils.Result
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AddViewModel(private val itemsRepository: ItemsRepository) : ViewModel() {
    var resultAddItem = MutableLiveData<Result<AddItemResponse?>>()
    val resultItemId = MutableLiveData<Result<GenerateIdResponse>>()

    fun addItem(token: String, request: AddItemRequest) {
        viewModelScope.launch {
            itemsRepository.addItem(token, request).collect {
                resultAddItem.value = it
            }
        }
    }

    fun getItemId() {
        viewModelScope.launch {
            itemsRepository.generateItemId().collect {
                resultItemId.value = it
            }
        }
    }

}