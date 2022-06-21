package com.vincent.givetake.data.repository.otp

import com.vincent.givetake.data.repository.items.ItemsRepository
import com.vincent.givetake.data.source.api.ItemsService
import com.vincent.givetake.data.source.api.OtpService
import com.vincent.givetake.data.source.request.OtpRequest
import com.vincent.givetake.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class OtpRepository(private val apiService: OtpService) {

    fun sendOtp(body: OtpRequest) = flow {
        emit(Result.Loading)
        val response = apiService.sendOtp(body)
        if (response.isSuccessful) {
            if (response.body()?.error?.message.isNullOrEmpty()) {
                emit(Result.Success(response.body()))
            }else {
                emit(Result.Error(response.body()!!.error.message))
            }
        }else {
            emit(Result.Error(response.message()))
        }
    }.catch { emit(Result.Error("Server timeout. Silahkan dicoba kembali beberapa saat lagi"))
    }.flowOn(Dispatchers.IO)

    companion object {

        @Volatile
        private var instance: OtpRepository? = null

        fun getInstance(
            apiService: OtpService,
        ) : OtpRepository {
            return instance ?: synchronized(this) {
                OtpRepository(apiService).also {
                    instance = it
                }
            }
        }
    }
}