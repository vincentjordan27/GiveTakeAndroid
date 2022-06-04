package com.vincent.givetake.data.source.api

import com.vincent.givetake.data.source.request.CreateChatRequest
import com.vincent.givetake.data.source.response.chat.AllChatListResponse
import com.vincent.givetake.data.source.response.chat.CreateChatRoomResponse
import com.vincent.givetake.data.source.response.chat.UpdateChatResponse
import retrofit2.Response
import retrofit2.http.*

interface ChatService {
    @POST("chat")
    suspend fun createChatRoom(
        @Header("Authorization") auth: String,
        @Body body: CreateChatRequest
    ): Response<CreateChatRoomResponse>

    @GET("chat")
    suspend fun getChatList(
        @Header("Authorization") auth: String,
    ) : Response<AllChatListResponse>

    @PATCH("chat/{id}")
    suspend fun validChat(
        @Header("Authorization") auth: String,
        @Path("id") id: String
    ): Response<UpdateChatResponse>
}

