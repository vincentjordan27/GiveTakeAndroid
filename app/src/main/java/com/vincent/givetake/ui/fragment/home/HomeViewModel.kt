package com.vincent.givetake.ui.fragment.home

import androidx.lifecycle.*
import com.vincent.givetake.data.repository.items.ItemsRepository
import com.vincent.givetake.data.source.response.items.AllItemResponse
import com.vincent.givetake.preference.UserPreferences
import com.vincent.givetake.utils.Result
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class HomeViewModel(private val itemsRepository: ItemsRepository, private val pref: UserPreferences) : ViewModel() {

    var resultLogin = MutableLiveData<Result<AllItemResponse>>()

    fun getAllItemsLogin(key: String) {
        viewModelScope.launch {
            itemsRepository.getAllItemsLogin(key).collect {
                resultLogin.value = it
            }
        }
    }

    fun getAccessKey() : LiveData<String> {
        return pref.getUserAccessKey().asLiveData()
    }

    fun getUserId() : LiveData<String> {
        return pref.getUserId().asLiveData()
    }
}