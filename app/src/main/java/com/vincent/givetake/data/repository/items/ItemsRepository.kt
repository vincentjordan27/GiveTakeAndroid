package com.vincent.givetake.data.repository.items

import com.google.gson.Gson
import com.vincent.givetake.data.source.api.ItemsService
import com.vincent.givetake.data.source.request.AddItemRequest
import com.vincent.givetake.data.source.request.WishlistRequest
import com.vincent.givetake.data.source.request.DeleteItemImageRequest
import com.vincent.givetake.data.source.request.EditItemRequest
import com.vincent.givetake.data.source.response.items.*
//import com.vincent.givetake.data.source.response.DetailLoginResponse
import com.vincent.givetake.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class ItemsRepository(private val apiService: ItemsService ) {

    fun getAllItemsLogin(token: String) = flow {
        emit(Result.Loading)
        emit(Result.Success(apiService.getAllItemsLogin(token)))
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
    }.catch { emit(Result.Error(it.message.toString()))
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

    fun uploadImageItem(token: String, itemId: String, file: File) = flow {
        emit(Result.Loading)
        val requestBody = file.asRequestBody("image/*".toMediaType())
        val imageData = MultipartBody.Part.createFormData("data", filename = file.name, requestBody)
        val response = apiService.uploadItemImage(token, itemId, imageData)
        if (response.isSuccessful) {
            emit(Result.Success(response.body()))
        } else {
            val errorResponse = Gson().fromJson(response.errorBody()!!.string(), AddItemImageResponse::class.java)
            emit(Result.Error(errorResponse.message))
        }
    }.flowOn(Dispatchers.IO)

    fun deleteImageItem(token: String, itemId: String, body: DeleteItemImageRequest) = flow {
        emit(Result.Loading)
        emit(Result.Success(apiService.deleteItemImage(token, itemId, body)))
    }.catch { emit(Result.Error(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    fun updateItem(token: String, itemId: String, body: EditItemRequest) = flow {
        emit(Result.Loading)
        val response = apiService.updateItem(token, itemId, body)
        if (response.isSuccessful) {
            emit(Result.Success(response.body()))
        }else {
            val errorResponse = Gson().fromJson(response.errorBody()!!.string(), UpdateItemResponse::class.java)
            emit(Result.Error(errorResponse.message))
        }
    }.flowOn(Dispatchers.IO)

    fun getMyOffers(token: String) = flow {
        emit(Result.Loading)
        val response = apiService.getMyOffers(token)
        if (response.isSuccessful) {
            emit(Result.Success(response.body()))
        }else {
            val errorResponse = Gson().fromJson(response.errorBody()!!.string(), MyOffersResponse::class.java)
            emit(Result.Error(errorResponse.message))
        }
    }.flowOn(Dispatchers.IO)

    fun getMyItems(token: String) = flow {
        emit(Result.Loading)
        val response = apiService.getMyItems(token)
        if (response.isSuccessful) {
            emit(Result.Success(response.body()))
        }else {
            val errorResponse = Gson().fromJson(response.errorBody()!!.string(), MyItemsResponse::class.java)
            emit(Result.Error(errorResponse.message))
        }
    }.flowOn(Dispatchers.IO)

    fun addWishlist(token: String, body: WishlistRequest) = flow {
        emit(Result.Loading)
        val response = apiService.addWishlist(token, body)
        if (response.isSuccessful) {
            emit(Result.Success(response.body()))
        }else {
            val errorResponse = Gson().fromJson(response.errorBody()!!.string(), WishlistResponse::class.java)
            emit(Result.Error(errorResponse.message))
        }
    }.flowOn(Dispatchers.IO)

    fun deleteWishlist(token: String, body: WishlistRequest) = flow {
        emit(Result.Loading)
        val response = apiService.deleteWishlist(token, body)
        if (response.isSuccessful) {
            emit(Result.Success(response.body()))
        }else {
            val errorResponse = Gson().fromJson(response.errorBody()!!.string(), WishlistResponse::class.java)
            emit(Result.Error(errorResponse.message))
        }
    }.flowOn(Dispatchers.IO)

    fun getWishlist(token: String) = flow {
        emit(Result.Loading)
        val response = apiService.getWishlist(token)
        if (response.isSuccessful) {
            emit(Result.Success(response.body()))
        }else {
            val errorResponse = Gson().fromJson(response.errorBody()!!.string(), MyWishlistResponse::class.java)
            emit(Result.Error(errorResponse.message))
        }
    }.flowOn(Dispatchers.IO)

    companion object {

        @Volatile
        private var instance: ItemsRepository? = null

        fun getInstance(
            apiService: ItemsService,
        ) : ItemsRepository {
            return instance ?: synchronized(this) {
                ItemsRepository(apiService).also {
                    instance = it
                }
            }
        }
    }
}