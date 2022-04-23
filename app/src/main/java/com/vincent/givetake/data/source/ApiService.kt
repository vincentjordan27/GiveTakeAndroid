package com.vincent.givetake.data.source

import com.vincent.givetake.data.source.request.AddItemRequest
import com.vincent.givetake.data.source.request.LoginRequest
import com.vincent.givetake.data.source.request.RegisterRequest
import com.vincent.givetake.data.source.response.items.*
import com.vincent.givetake.data.source.response.users.LoginResponse
import com.vincent.givetake.data.source.response.users.RegisterResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Users
    @POST("register")
    suspend fun registerUser(
        @Body body: RegisterRequest
    ) : Response<RegisterResponse>

    @POST("login")
    suspend fun loginUser(
        @Body body: LoginRequest
    ) : Response<LoginResponse>

    // Items
    @GET("items")
    suspend fun getAllItems() : AllItemResponse

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("itemsLog")
    suspend fun getAllItemsLogin(
        @Header("Authorization") auth: String,
    ) : AllItemResponse

    @GET("itemnolog/{id}")
    suspend fun getItemByIdNotLogin(
        @Path("id") id: String
    ) : DetailResponseNonLogin

    @GET("item/{id}")
    suspend fun getItemById(
        @Path("id") id: String,
        @Header("Authorization") auth: String
    ) : Response<DetailResponseLogin>

    @GET("/itemid")
    suspend fun generateId() : GenerateIdResponse

    @POST("item")
    suspend fun addItem(
        @Body item: AddItemRequest,
        @Header("Authorization") auth: String
    ) : Response<AddItemResponse>

    @DELETE("item/{id}")
    suspend fun deleteItem(
        @Path("id") id: String,
        @Header("Authorization") auth: String
    ) : Response<DeleteItemResponse>
}