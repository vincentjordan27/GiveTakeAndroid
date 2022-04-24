package com.vincent.givetake.data.source.response.items

data class AddItemImageResponse(
    val status: String,
    val data: ItemImage,
    val message: String
)

data class ItemImage(
    val imageId: String,
    val fileLocation: String,
)
