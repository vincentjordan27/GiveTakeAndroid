package com.vincent.givetake.data.repository.email

import com.google.gson.Gson
import com.vincent.givetake.data.source.api.EmailService
import com.vincent.givetake.data.source.request.ResetRequest
import com.vincent.givetake.data.source.response.items.StatusResponse
import com.vincent.givetake.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class EmailRepository(private val apiService: EmailService) {

    fun resetPhone(accessKey: String, body: ResetRequest) = flow {
        emit(Result.Loading)
        val response = apiService.resetPhone(accessKey, body)
        if (response.isSuccessful) {
            emit(Result.Success(response.body()))
        }else {
            val errorResponse = Gson().fromJson(response.errorBody()!!.string(), StatusResponse::class.java)
            emit(Result.Error(errorResponse.message))
        }
    }.catch { emit(Result.Error("Server timeout. Silahkan dicoba kembali beberapa saat lagi"))
    }.flowOn(Dispatchers.IO)

    fun resetPass(username: String, body: ResetRequest) = flow {
        emit(Result.Loading)
        val response = apiService.resetPass(username, body)
        if (response.isSuccessful) {
            emit(Result.Success(response.body()))
        } else {
            val errorResponse = Gson().fromJson(response.errorBody()!!.string(), StatusResponse::class.java)
            emit(Result.Error(errorResponse.message))
        }
    }.catch { emit(Result.Error("Server timeout. Silahkan dicoba kembali beberapa saat lagi"))
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