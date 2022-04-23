package com.vincent.givetake.data.repository.users

import com.google.gson.Gson
import com.vincent.givetake.data.source.ApiService
import com.vincent.givetake.data.source.request.LoginRequest

import com.vincent.givetake.data.source.request.RegisterRequest
import com.vincent.givetake.data.source.response.users.LoginResponse
import com.vincent.givetake.data.source.response.users.RegisterResponse
import com.vincent.givetake.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class UsersRepository (private val apiService: ApiService) {

    fun registerUser(data: RegisterRequest) = flow {
        emit(Result.Loading)
        val response = apiService.registerUser(data)
        if (response.isSuccessful) {
            emit(Result.Success(response.body()))
        }else {
            val errorResponse = Gson().fromJson(response.errorBody()!!.string(), RegisterResponse::class.java)
            emit(Result.Error(errorResponse.message))
        }
    }.flowOn(Dispatchers.IO)

    fun loginUser(data: LoginRequest) = flow {
        emit(Result.Loading)
        val response = apiService.loginUser(data)
        if (response.isSuccessful) {
            emit(Result.Success(response.body()))
        }else {
            val errorResponse = Gson().fromJson(response.errorBody()!!.string(), LoginResponse::class.java)
            emit(Result.Error(errorResponse.message))
        }
    }.flowOn(Dispatchers.IO)

    companion object {

        @Volatile
        private var instance: UsersRepository? = null

        fun getInstance(
            apiService: ApiService,
        ) : UsersRepository {
            return instance ?: synchronized(this) {
                UsersRepository(apiService).also {
                    instance = it
                }
            }
        }
    }
}