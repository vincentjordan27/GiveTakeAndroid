package com.vincent.givetake.data.source.request

data class CreateChatRequest(
    val receiverId: String,
    val itemsId: String,
)