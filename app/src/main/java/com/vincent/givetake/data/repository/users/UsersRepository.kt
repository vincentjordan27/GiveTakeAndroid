package com.vincent.givetake.data.repository.users

import com.google.gson.Gson
import com.vincent.givetake.data.source.api.UsersService
import com.vincent.givetake.data.source.request.LoginRequest

import com.vincent.givetake.data.source.request.RegisterRequest
import com.vincent.givetake.data.source.request.UpdateProfileRequest
import com.vincent.givetake.data.source.response.users.LoginResponse
import com.vincent.givetake.data.source.response.users.RegisterResponse
import com.vincent.givetake.data.source.response.users.UpdateUserResponse
import com.vincent.givetake.data.source.response.users.UserDataResponse
import com.vincent.givetake.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class UsersRepository (private val apiService: UsersService) {

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

    fun userData(token: String) = flow {
        emit(Result.Loading)
        val response = apiService.userData(token)
        if (response.isSuccessful) {
            emit(Result.Success(response.body()))
        } else {
            val errorResponse = Gson().fromJson(response.errorBody()!!.string(), UserDataResponse::class.java)
            emit(Result.Error(errorResponse.message))
        }
    }.flowOn(Dispatchers.IO)

    fun updateProfile(token: String, body: UpdateProfileRequest) = flow {
        emit(Result.Loading)
        val response = apiService.updateProfile(token, body)
        if (response.isSuccessful) {
            emit(Result.Success(response.body()))
        } else {
            val errorResponse = Gson().fromJson(response.errorBody()!!.string(), UpdateUserResponse::class.java)
            emit(Result.Error(errorResponse.message))
        }
    }.flowOn(Dispatchers.IO)

    fun uploadImageProfile(file: File) = flow {
        emit(Result.Loading)
        val requestBody = file.asRequestBody("image/*".toMediaType())
        val imageData = MultipartBody.Part.createFormData("data", filename = file.name, requestBody)
        val response = apiService.uploadProfileImage(imageData)
        if (response.isSuccessful) {
            emit(Result.Success(response.body()))
        }else {
            val errorResponse = response.errorBody().toString()
            emit(Result.Error(errorResponse))
        }
    }.catch { emit(Result.Error(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    companion object {

        @Volatile
        private var instance: UsersRepository? = null

        fun getInstance(
            apiService: UsersService,
        ) : UsersRepository {
            return instance ?: synchronized(this) {
                UsersRepository(apiService).also {
                    instance = it
                }
            }
        }
    }
}