package com.vincent.givetake.ui.fragment.chat

import androidx.lifecycle.*
import com.vincent.givetake.data.repository.chat.ChatRepository
import com.vincent.givetake.data.source.response.chat.AllChatListResponse
import com.vincent.givetake.data.source.response.chat.UpdateChatResponse
import com.vincent.givetake.preference.UserPreferences
import com.vincent.givetake.utils.Result
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ChatViewModel(val repository: ChatRepository, val preferences: UserPreferences) : ViewModel() {

    val resultAllChat = MutableLiveData<Result<AllChatListResponse?>>()

    fun getUserId() : LiveData<String> {
        return preferences.getUserId().asLiveData()
    }

    fun getAccessKey() : LiveData<String> {
        return preferences.getUserAccessKey().asLiveData()
    }

    fun getAllChatRoom(token: String) = viewModelScope.launch {
        repository.getChatRoom(token).collect {
            resultAllChat.value = it
        }
    }
}