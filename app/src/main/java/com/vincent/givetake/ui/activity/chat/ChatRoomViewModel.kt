package com.vincent.givetake.ui.activity.chat

import androidx.lifecycle.*
import com.vincent.givetake.data.repository.chat.ChatRepository
import com.vincent.givetake.data.repository.items.ItemsRepository
import com.vincent.givetake.data.repository.users.UsersRepository
import com.vincent.givetake.data.source.response.chat.UpdateChatResponse
import com.vincent.givetake.data.source.response.items.DetailResponseLogin
import com.vincent.givetake.data.source.response.users.TokenResponse
import com.vincent.givetake.preference.UserPreferences
import com.vincent.givetake.utils.Result
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ChatRoomViewModel(private val chatRepository: ChatRepository, private val repository: UsersRepository, private val itemsRepository: ItemsRepository, private val preferences: UserPreferences) : ViewModel() {


    var resultLogin = MutableLiveData<Result<DetailResponseLogin?>>()
    val resultUpdateChat = MutableLiveData<Result<UpdateChatResponse?>>()


    fun getDetailLogin(id: String, token: String) {
        viewModelScope.launch {
            itemsRepository.getItemByIdLogin(token, id).collect {
                resultLogin.value = it
            }
        }
    }

    fun validateChat(token: String, id: String) = viewModelScope.launch {
        chatRepository.validateChat(token, id).collect {
            resultUpdateChat.value = it
        }
    }


    fun getUserId() : LiveData<String> {
        return preferences.getUserId().asLiveData()
    }


}