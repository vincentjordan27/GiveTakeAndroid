package com.vincent.givetake.data.source.response.items

data class DeleteItemResponse(
    val status: String,
    val data: DeleteItemData,
    val message: String
)

data class DeleteItemData(
    val name: String,
)
