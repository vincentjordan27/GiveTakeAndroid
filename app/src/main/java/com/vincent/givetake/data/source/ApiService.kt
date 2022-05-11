package com.vincent.givetake.data.source

import com.vincent.givetake.data.source.request.*
import com.vincent.givetake.data.source.response.items.*
import com.vincent.givetake.data.source.response.users.*
import okhttp3.MultipartBody
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

    @GET("user")
    suspend fun userData(
        @Header("Authorization") auth: String
    ) : Response<UserDataResponse>

    @PATCH("profile")
    suspend fun updateProfile(
        @Header("Authorization") auth: String,
        @Body body: UpdateProfileRequest
    ) : Response<UpdateUserResponse>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("itemsLog")
    suspend fun getAllItemsLogin(
        @Header("Authorization") auth: String,
    ) : AllItemResponse

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

    @Multipart
    @POST("/upload/item/image/{id}")
    suspend fun uploadItemImage(
        @Header("Authorization") auth: String,
        @Path("id") id: String,
        @Part data : MultipartBody.Part
    ) : Response<AddItemImageResponse>

    @HTTP(method = "DELETE", path = "/upload/item/image/{id}", hasBody = true)
    suspend fun deleteItemImage(
        @Header("Authorization") auth: String,
        @Path("id") id: String,
        @Body item: DeleteItemImageRequest
    ) : DeleteItemImageResponse

    @Multipart
    @POST("/upload/user")
    suspend fun uploadProfileImage(
        @Part data: MultipartBody.Part
    ) : Response<UploadProfileImageResponse>
}