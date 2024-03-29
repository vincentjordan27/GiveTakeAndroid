package com.vincent.givetake.data.source.api

import com.vincent.givetake.data.source.request.*
import com.vincent.givetake.data.source.response.items.StatusResponse
import com.vincent.givetake.data.source.response.users.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface UsersService {
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

    @Multipart
    @POST("/upload/user")
    suspend fun uploadProfileImage(
        @Part data: MultipartBody.Part
    ) : Response<UploadProfileImageResponse>

    @PATCH("token")
    suspend fun updateToken(
        @Header("Authorization") auth: String,
        @Body body: UpdateTokenRequest
    ) : Response<StatusResponse>

    @PATCH("phone")
    suspend fun updatePhone(
        @Header("Authorization") auth: String,
        @Body body: UpdatePhoneRequest
    ) : Response<StatusResponse>

    @PATCH("password/{username}")
    suspend fun updatePass(
        @Path("username") username: String,
        @Body body: UpdatePassRequest
    ) : Response<StatusResponse>
}