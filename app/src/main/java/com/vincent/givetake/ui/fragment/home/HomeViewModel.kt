package com.vincent.givetake.ui.fragment.home

import androidx.lifecycle.*
import com.vincent.givetake.data.repository.items.ItemsRepository
import com.vincent.givetake.data.repository.users.UsersRepository
import com.vincent.givetake.data.source.request.FilterRequest
import com.vincent.givetake.data.source.request.UpdateTokenRequest
import com.vincent.givetake.data.source.response.items.AllItemResponse
import com.vincent.givetake.data.source.response.items.StatusResponse
import com.vincent.givetake.preference.FilterData
import com.vincent.givetake.preference.UserPreferences
import com.vincent.givetake.utils.Result
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch


class HomeViewModel(private val itemsRepository: ItemsRepository, private val usersRepository: UsersRepository, private val pref: UserPreferences) : ViewModel() {

    var resultLogin = MutableLiveData<Result<AllItemResponse>>()
    var resultFilterLogin = MutableLiveData<Result<AllItemResponse?>>()
    var resultSearchItem = MutableLiveData<Result<AllItemResponse?>>()
    var resultUpdateToken = MutableLiveData<Result<StatusResponse?>>()

    fun getAllItemsLogin(key: String) {
        viewModelScope.launch {
            itemsRepository.getAllItemsLogin(key).collect {
                resultLogin.value = it
            }
        }
    }

    fun getAllItemsFilter(key: String, body: FilterRequest) = viewModelScope.launch {
        itemsRepository.getItemFilter(key, body).collect {
            resultFilterLogin.value = it
        }
    }

    fun getAllItemsSearch(key: String, query: String) = viewModelScope.launch {
        itemsRepository.getItemSearch(key, query).collect {
            resultSearchItem.value = it
            pref.reset()
        }
    }

    fun updateToken(key: String, body: UpdateTokenRequest) = viewModelScope.launch {
        usersRepository.updateToken(key, body).collect {
            resultUpdateToken.value = it
        }
    }

    fun getAccessKey() : LiveData<String> {
        return pref.getUserAccessKey().asLiveData()
    }

    fun getUserId() : LiveData<String> {
        return pref.getUserId().asLiveData()
    }

    fun getFilter() : LiveData<FilterData> {
        return pref.getFilterData().asLiveData()
    }


}