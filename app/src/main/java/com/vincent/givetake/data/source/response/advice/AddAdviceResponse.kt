package com.vincent.givetake.data.source.response.advice

data class AddAdviceResponse(
    val status: String,
    val data: AddAdviceData,
    val message: String
)

data class AddAdviceData(
    val adviceId: String,
)