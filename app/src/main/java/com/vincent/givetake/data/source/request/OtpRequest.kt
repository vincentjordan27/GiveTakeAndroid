package com.vincent.givetake.data.source.request

data class OtpRequest(
    val phone: String,
    val messageType: String,
    val body: String,
)