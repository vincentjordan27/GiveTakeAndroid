package com.vincent.givetake.data.repository.items

import com.google.gson.Gson
import com.vincent.givetake.data.source.ApiService
import com.vincent.givetake.data.source.request.AddItemRequest
import com.vincent.givetake.data.source.response.items.AddItemResponse
import com.vincent.givetake.data.source.response.items.DeleteItemResponse
import com.vincent.givetake.data.source.response.items.DetailResponseLogin
//import com.vincent.givetake.data.source.response.DetailLoginResponse
import com.vincent.givetake.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ItemsRepository(private val apiService: ApiService, ) {

    fun getAllItemsNotLogin() = flow {
        emit(Result.Loading)
        emit(Result.Success(apiService.getAllItems()))
    }.catch { emit(Result.Error(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    fun getAllItemsLogin(token: String) = flow {
        emit(Result.Loading)
        emit(Result.Success(apiService.getAllItemsLogin(token)))
    }.catch { emit(Result.Error(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    fun getItemByIdGuest(id: String) = flow {
        emit(Result.Loading)
        emit(Result.Success(apiService.getItemByIdNotLogin(id)))
    }.catch { emit(Result.Error(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    fun getItemByIdLogin(token: String, id: String) = flow {
        emit(Result.Loading)
        val response = apiService.getItemById(id, token)
        if (response.isSuccessful) {
            emit(Result.Success(response.body()))
        }else {
            val errorResponse = Gson().fromJson(response.errorBody()!!.string(), DetailResponseLogin::class.java)
            emit(Result.Error(errorResponse.message))
        }
    }.flowOn(Dispatchers.IO)

    fun generateItemId() = flow {
        emit(Result.Loading)
        emit(Result.Success(apiService.generateId()))
    }.flowOn(Dispatchers.IO)

    fun addItem(token: String, addItemRequest: AddItemRequest) = flow {
        emit(Result.Loading)
        val response = apiService.addItem(addItemRequest, token)
        if (response.isSuccessful) {
            emit(Result.Success(response.body()))
        }else {
            val errorResponse = Gson().fromJson(response.errorBody()!!.string(), AddItemResponse::class.java)
            emit(Result.Error(errorResponse.message))
        }
    }.flowOn(Dispatchers.IO)

    fun deleteItem(id: String, token: String) = flow {
        emit(Result.Loading)
        val response = apiService.deleteItem(id, token)
        if (response.isSuccessful) {
            emit(Result.Success(response.body()))
        }else {
            val errorResponse = Gson().fromJson(response.errorBody()!!.string(), DeleteItemResponse::class.java)
            emit(Result.Error(errorResponse.message))
        }
    }.flowOn(Dispatchers.IO)

    companion object {

        @Volatile
        private var instance: ItemsRepository? = null

        fun getInstance(
            apiService: ApiService,
        ) : ItemsRepository {
            return instance ?: synchronized(this) {
                ItemsRepository(apiService).also {
                    instance = it
                }
            }
        }
    }
}