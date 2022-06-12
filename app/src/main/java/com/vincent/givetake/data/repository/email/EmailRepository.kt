package com.vincent.givetake.data.repository.email

import com.google.gson.Gson
import com.vincent.givetake.data.repository.chat.ChatRepository
import com.vincent.givetake.data.source.api.ChatService
import com.vincent.givetake.data.source.api.EmailService
import com.vincent.givetake.data.source.request.ResetPhoneRequest
import com.vincent.givetake.data.source.response.chat.CreateChatRoomResponse
import com.vincent.givetake.data.source.response.items.StatusResponse
import com.vincent.givetake.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class EmailRepository(private val apiService: EmailService) {

    fun resetPhone(accessKey: String, body: ResetPhoneRequest) = flow {
        emit(Result.Loading)
        val response = apiService.resetPhone(accessKey, body)
        if (response.isSuccessful) {
            emit(Result.Success(response.body()))
        }else {
            val errorResponse = Gson().fromJson(response.errorBody()!!.string(), StatusResponse::class.java)
            emit(Result.Error(errorResponse.message))
        }
    }.flowOn(Dispatchers.IO)


    companion object {
        @Volatile
        private var instance: EmailRepository? = null

        fun getInstance(
            apiService: EmailService,
        ) : EmailRepository {
            return instance ?: synchronized(this) {
                EmailRepository(apiService).also {
                    instance = it
                }
            }
        }
    }

}