package com.vincent.givetake.data.repository.advice

import com.google.gson.Gson
import com.vincent.givetake.data.repository.chat.ChatRepository
import com.vincent.givetake.data.source.api.AdviceService
import com.vincent.givetake.data.source.api.ChatService
import com.vincent.givetake.data.source.request.AddAdviceRequest
import com.vincent.givetake.data.source.response.advice.AddAdviceResponse
import com.vincent.givetake.data.source.response.advice.AllAdviceResponse
import com.vincent.givetake.data.source.response.chat.CreateChatRoomResponse
import com.vincent.givetake.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class AdviceRepository(private val apiService: AdviceService) {

    fun addAdvice(token: String, body: AddAdviceRequest) = flow {
        emit(Result.Loading)
        val response = apiService.sendAdvice(token, body)
        if (response.isSuccessful) {
            emit(Result.Success(response.body()))
        }else {
            val errorResponse = Gson().fromJson(response.errorBody()!!.string(), AddAdviceResponse::class.java)
            emit(Result.Error(errorResponse.message))
        }
    }.catch { emit(Result.Error("Server timeout. Silahkan dicoba kembali beberapa saat lagi"))
    }.flowOn(Dispatchers.IO)

    fun myAdvices(token: String) = flow {
        emit(Result.Loading)
        val response = apiService.myAdvices(token)
        if (response.isSuccessful) {
            emit(Result.Success(response.body()))
        }else {
            val errorResponse = Gson().fromJson(response.errorBody()!!.string(), AllAdviceResponse::class.java)
            emit(Result.Error(errorResponse.message))
        }
    }.catch { emit(Result.Error("Server timeout. Silahkan dicoba kembali beberapa saat lagi"))
    }.flowOn(Dispatchers.IO)

    companion object {
        @Volatile
        private var instance: AdviceRepository? = null

        fun getInstance(
            apiService: AdviceService,
        ) : AdviceRepository {
            return instance ?: synchronized(this) {
                AdviceRepository(apiService).also {
                    instance = it
                }
            }
        }
    }
}