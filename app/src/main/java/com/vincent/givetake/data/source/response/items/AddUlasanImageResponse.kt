package com.vincent.givetake.data.source.response.items

data class AddUlasanImageResponse(
    val status: String,
    val data: UlasanImageData,
    val message: String
)

data class UlasanImageData(
    val imageId: String,
    val fileLocation: String
)