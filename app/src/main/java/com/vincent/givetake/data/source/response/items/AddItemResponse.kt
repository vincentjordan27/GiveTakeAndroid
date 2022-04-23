package com.vincent.givetake.data.source.response.items

data class AddItemResponse(
    val status: String,
    val data: DataAddItemResponse,
    val message: String
)

data class DataAddItemResponse(
    val name: String
)

