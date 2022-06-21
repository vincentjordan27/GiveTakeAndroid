package com.vincent.givetake.data.repository.chat

import com.google.gson.Gson
import com.vincent.givetake.data.source.api.ChatService
import com.vincent.givetake.data.source.request.CreateChatRequest
import com.vincent.givetake.data.source.response.chat.AllChatListResponse
import com.vincent.givetake.data.source.response.chat.CreateChatRoomResponse
import com.vincent.givetake.data.source.response.chat.UpdateChatResponse
import com.vincent.givetake.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ChatRepository(private val apiService: ChatService) {

    fun createChatRoom(token: String, body: CreateChatRequest) = flow {
        emit(Result.Loading)
        val response = apiService.createChatRoom(token, body)
        if (response.isSuccessful) {
            emit(Result.Success(response.body()))
        }else {
            val errorResponse = Gson().fromJson(response.errorBody()!!.string(), CreateChatRoomResponse::class.java)
            emit(Result.Error(errorResponse.message))
        }
    }.catch { emit(Result.Error("Server timeout. Silahkan dicoba kembali beberapa saat lagi"))
    }.flowOn(Dispatchers.IO)

    fun getChatRoom(token: String) = flow {
        emit(Result.Loading)
        val response = apiService.getChatList(token)
        if (response.isSuccessful) {
            emit(Result.Success(response.body()))
        } else {
            val errorResponse = Gson().fromJson(response.errorBody()!!.string(), AllChatListResponse::class.java)
            emit(Result.Error(errorResponse.message))
        }
    }.catch { emit(Result.Error("Server timeout. Silahkan dicoba kembali beberapa saat lagi"))
    }.flowOn(Dispatchers.IO)

    fun validateChat(token: String, id: String) = flow {
        emit(Result.Loading)
        val response = apiService.validChat(token, id)
        if (response.isSuccessful) {
            emit(Result.Success(response.body()))
        }else {
            val errorResponse = Gson().fromJson(response.errorBody()!!.string(), UpdateChatResponse::class.java)
            emit(Result.Error(errorResponse.message))
        }
    }.catch { emit(Result.Error("Server timeout. Silahkan dicoba kembali beberapa saat lagi"))
    }.flowOn(Dispatchers.IO)

    companion object {
        @Volatile
        private var instance: ChatRepository? = null

        fun getInstance(
            apiService: ChatService,
        ) : ChatRepository {
            return instance ?: synchronized(this) {
                ChatRepository(apiService).also {
                    instance = it
                }
            }
        }
    }
}
