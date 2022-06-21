package com.vincent.givetake.data.repository.users

import com.google.gson.Gson
import com.vincent.givetake.data.source.api.UsersService
import com.vincent.givetake.data.source.request.*

import com.vincent.givetake.data.source.response.items.StatusResponse
import com.vincent.givetake.data.source.response.users.*
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
    }.catch { emit(Result.Error("Server timeout. Silahkan dicoba kembali beberapa saat lagi"))
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
    }.catch { emit(Result.Error("Server timeout. Silahkan dicoba kembali beberapa saat lagi"))
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
    }.catch {
        emit(Result.Error("Server timeout. Silahkan dicoba kembali beberapa saat lagi"))
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
    }.catch {
        emit(Result.Error("Server timeout. Silahkan dicoba kembali beberapa saat lagi"))
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
    }.catch {
        emit(Result.Error("Server timeout. Silahkan dicoba kembali beberapa saat lagi"))
    }.flowOn(Dispatchers.IO)


    fun updatePhone(accessKey: String, body: UpdatePhoneRequest) = flow {
        emit(Result.Loading)
        val response = apiService.updatePhone(accessKey, body)
        if (response.isSuccessful) {
            emit(Result.Success(response.body()))
        }else {
            val errorResponse = Gson().fromJson(response.errorBody()!!.string(), StatusResponse::class.java)
            emit(Result.Error(errorResponse.message))
        }
    }.catch {
        emit(Result.Error("Server timeout. Silahkan dicoba kembali beberapa saat lagi"))
    }.flowOn(Dispatchers.IO)

    fun updateToken(accessKey: String, body: UpdateTokenRequest) = flow {
        emit(Result.Loading)
        val response = apiService.updateToken(accessKey, body)
        if (response.isSuccessful) {
            emit(Result.Success(response.body()))
        } else {
            val errorResponse = Gson().fromJson(response.errorBody()!!.string(), StatusResponse::class.java)
            emit(Result.Error(errorResponse.message))
        }
    }.catch { emit(Result.Error("Server timeout. Silahkan dicoba kembali beberapa saat lagi"))
    }.flowOn(Dispatchers.IO)

    fun updatePassword(username: String, body: UpdatePassRequest) = flow {
        emit(Result.Loading)
        val response = apiService.updatePass(username, body)
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