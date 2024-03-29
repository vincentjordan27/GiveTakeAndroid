package com.vincent.givetake.data.source.request

data class AddItemRequest(
    val id: String,
    val name: String,
    val desc: String,
    val category: String,
    val address: String,
    val latitude: String,
    val longitude: String,
    val maxRadius: String,
    val status: Int,
    val thumbnail: String,
)