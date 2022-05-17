package com.vincent.givetake.ui.activity.items.add.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vincent.givetake.data.repository.items.ItemsRepository
import com.vincent.givetake.data.source.request.AddItemRequest
import com.vincent.givetake.data.source.request.DeleteItemImageRequest
import com.vincent.givetake.data.source.response.items.AddItemImageResponse
import com.vincent.givetake.data.source.response.items.AddItemResponse
import com.vincent.givetake.data.source.response.items.DeleteItemImageResponse
import com.vincent.givetake.data.source.response.items.GenerateIdResponse
import com.vincent.givetake.utils.Result
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File

class AddViewModel(private val itemsRepository: ItemsRepository) : ViewModel() {
    var resultAddItem = MutableLiveData<Result<AddItemResponse?>>()
    val resultItemId = MutableLiveData<Result<GenerateIdResponse>>()
    val resultUploadImage = MutableLiveData<Result<AddItemImageResponse?>>()
    val resultDeleteImage = MutableLiveData<Result<DeleteItemImageResponse>>()

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

    fun uploadImageItem(token: String, itemId: String, file: File) {
        viewModelScope.launch {
            itemsRepository.uploadImageItem(token, itemId, file).collect {
                resultUploadImage.value = it
            }
        }
    }

    fun deleteImageItem(token: String, itemId: String, body: DeleteItemImageRequest) {
        viewModelScope.launch {
            itemsRepository.deleteImageItem(token, itemId, body).collect {
                resultDeleteImage.value = it
            }
        }
    }

}