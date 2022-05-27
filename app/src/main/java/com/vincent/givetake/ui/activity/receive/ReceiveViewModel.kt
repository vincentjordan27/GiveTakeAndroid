package com.vincent.givetake.ui.activity.receive

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vincent.givetake.data.repository.items.ItemsRepository
import com.vincent.givetake.data.source.request.DeleteUlasanImageRequest
import com.vincent.givetake.data.source.request.FinishReceiveItemRequest
import com.vincent.givetake.data.source.response.items.AddUlasanImageResponse
import com.vincent.givetake.data.source.response.items.StatusResponse
import com.vincent.givetake.utils.Result
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File

class ReceiveViewModel(private val repository: ItemsRepository) : ViewModel() {
    val uploadImageUlasanResult = MutableLiveData<Result<AddUlasanImageResponse?>>()
    val deleteImageUlasanResult = MutableLiveData<Result<StatusResponse?>>()
    val finishReceiveItemResult = MutableLiveData<Result<StatusResponse?>>()

    fun uploadImageUlasan(token: String, itemId: String, file: File) = viewModelScope.launch {
        repository.uploadImageUlasan(token, itemId, file).collect {
            uploadImageUlasanResult.value = it
        }
    }

    fun deleteImageUlasan(token: String, itemId: String, body: DeleteUlasanImageRequest) = viewModelScope.launch {
        repository.deleteImageUlasan(token, itemId, body).collect {
            deleteImageUlasanResult.value = it
        }
    }

    fun finishReceiveItem(token: String, itemId: String, body: FinishReceiveItemRequest) = viewModelScope.launch {
        repository.finishReceiveItem(token, itemId, body).collect {
            finishReceiveItemResult.value = it
        }
    }
}