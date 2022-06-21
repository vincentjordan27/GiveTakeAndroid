package com.vincent.givetake.data.source.response.advice

import com.google.gson.annotations.SerializedName

data class AllAdviceResponse(
    val status: String,
    val data: List<AdviceItemResponse>,
    val message: String,
)

data class AdviceItemResponse(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val reply: String = "",
    @field:SerializedName("user_id")
    val userId: String,
)
