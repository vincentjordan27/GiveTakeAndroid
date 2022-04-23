package com.vincent.givetake.ui.activity.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vincent.givetake.data.repository.items.ItemsRepository
import com.vincent.givetake.data.source.response.items.DeleteItemResponse
import com.vincent.givetake.data.source.response.items.DetailResponseLogin
import com.vincent.givetake.data.source.response.items.DetailResponseNonLogin
import com.vincent.givetake.utils.Result
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DetailViewModel(private val itemsRepository: ItemsRepository) : ViewModel() {
    var resultNotLogin = MutableLiveData<Result<DetailResponseNonLogin?>>()
    var resultLogin = MutableLiveData<Result<DetailResponseLogin?>>()
    var resultDelete = MutableLiveData<Result<DeleteItemResponse?>>()

    fun getDetailNonLogin(id: String) {
        viewModelScope.launch {
            itemsRepository.getItemByIdGuest(id).collect {
                resultNotLogin.value = it
            }
        }
    }

    fun getDetailLogin(id: String, token: String) {
        viewModelScope.launch {
            itemsRepository.getItemByIdLogin(token, id).collect {
                resultLogin.value = it
            }
        }
    }

    fun deleteItem(id: String, token: String){
        viewModelScope.launch {
            itemsRepository.deleteItem(id, token).collect {
                resultDelete.value = it
            }
        }
    }

}