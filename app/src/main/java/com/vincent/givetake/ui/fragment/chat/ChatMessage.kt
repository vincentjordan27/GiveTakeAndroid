package com.vincent.givetake.ui.fragment.chat

data class ChatMessage(
    val senderId: String,
    val receiverId: String,
    var message: String,
    val dateTime: String = "",
    var dateObject: Long = 0,
)
