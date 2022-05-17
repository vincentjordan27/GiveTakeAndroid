package com.vincent.givetake.ui.activity.items.edit

import androidx.lifecycle.*
import com.vincent.givetake.data.repository.items.ItemsRepository
import com.vincent.givetake.data.source.request.DeleteItemImageRequest
import com.vincent.givetake.data.source.request.EditItemRequest
import com.vincent.givetake.data.source.response.items.AddItemImageResponse
import com.vincent.givetake.data.source.response.items.DeleteItemImageResponse
import com.vincent.givetake.data.source.response.items.DetailResponseLogin
import com.vincent.givetake.data.source.response.items.UpdateItemResponse
import com.vincent.givetake.preference.UserPreferences
import com.vincent.givetake.utils.Result
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File

class EditViewModel(private val preferences: UserPreferences, private val repository: ItemsRepository) : ViewModel() {
    var resultDetail = MutableLiveData<Result<DetailResponseLogin?>>()
    val resultUploadImage = MutableLiveData<Result<AddItemImageResponse?>>()
    val resultDeleteImage = MutableLiveData<Result<DeleteItemImageResponse>>()
    val resultUpdateItem = MutableLiveData<Result<UpdateItemResponse?>>()

    fun getDetail(id: String, token: String) {
        viewModelScope.launch {
            repository.getItemByIdLogin(token, id).collect {
                resultDetail.value = it
            }
        }
    }

    fun uploadImageItem(token: String, itemId: String, file: File) {
        viewModelScope.launch {
            repository.uploadImageItem(token, itemId, file).collect {
                resultUploadImage.value = it
            }
        }
    }

    fun deleteImageItem(token: String, itemId: String, body: DeleteItemImageRequest) {
        viewModelScope.launch {
            repository.deleteImageItem(token, itemId, body).collect {
                resultDeleteImage.value = it
            }
        }
    }

    fun updateItem(token: String, itemId: String, body: EditItemRequest) = viewModelScope.launch {
        repository.updateItem(token, itemId, body).collect {
            resultUpdateItem.value = it
        }
    }
}