package com.vincent.givetake.ui.fragment.items

import androidx.lifecycle.*
import com.vincent.givetake.data.repository.items.ItemsRepository
import com.vincent.givetake.data.source.response.items.MyItemsResponse
import com.vincent.givetake.data.source.response.items.MyOffersResponse
import com.vincent.givetake.data.source.response.items.MyWishlistResponse
import com.vincent.givetake.preference.UserPreferences
import com.vincent.givetake.utils.Result
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ItemsViewModel(private val repository: ItemsRepository, private val userPreferences: UserPreferences) : ViewModel() {

    val myOfferResult = MutableLiveData<Result<MyOffersResponse?>>()
    val myItemsResult = MutableLiveData<Result<MyItemsResponse?>>()
    var resultGetWishlist = MutableLiveData<Result<MyWishlistResponse?>>()

    fun getMyOffers(token: String) = viewModelScope.launch {
        repository.getMyOffers(token).collect {
            myOfferResult.value = it
        }
    }

    fun getMyItems(token: String) = viewModelScope.launch {
        repository.getMyItems(token).collect {
            myItemsResult.value = it
        }
    }

    fun getAccessKey() : LiveData<String> {
        return userPreferences.getUserAccessKey().asLiveData()
    }

    fun getWishlist(token: String) = viewModelScope.launch {
        repository.getWishlist(token).collect {
            resultGetWishlist.value = it
        }
    }


}