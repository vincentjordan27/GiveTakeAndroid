package com.vincent.givetake.data.source.response.chat

data class CreateChatRoomResponse(
    val status: String,
    val chatId: String,
    val message: String,
)
