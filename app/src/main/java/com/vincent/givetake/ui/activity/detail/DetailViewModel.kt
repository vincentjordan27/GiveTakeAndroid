package com.vincent.givetake.ui.activity.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vincent.givetake.data.repository.items.ItemsRepository
import com.vincent.givetake.data.source.request.WishlistRequest
import com.vincent.givetake.data.source.response.items.WishlistResponse
import com.vincent.givetake.data.source.response.items.DeleteItemResponse
import com.vincent.givetake.data.source.response.items.DetailResponseLogin
import com.vincent.givetake.utils.Result
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DetailViewModel(private val itemsRepository: ItemsRepository) : ViewModel() {
    var resultDelete = MutableLiveData<Result<DeleteItemResponse?>>()
    var resultLogin = MutableLiveData<Result<DetailResponseLogin?>>()
    var resultAddWishlist = MutableLiveData<Result<WishlistResponse?>>()
    var resultDeleteWishlist = MutableLiveData<Result<WishlistResponse?>>()


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

    fun addWishlist(token: String, body: WishlistRequest) = viewModelScope.launch {
        itemsRepository.addWishlist(token, body).collect {
            resultAddWishlist.value = it
        }
    }

    fun deleteWishlist(token: String, body: WishlistRequest) = viewModelScope.launch {
        itemsRepository.deleteWishlist(token, body).collect {
            resultDeleteWishlist.value = it
        }
    }

}