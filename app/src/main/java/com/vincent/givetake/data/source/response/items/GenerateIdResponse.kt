package com.vincent.givetake.data.source.response.items

data class GenerateIdResponse(
    val status: String,
    val data: DataItemId
)

data class DataItemId(
    val itemId: String
)
