package com.vincent.givetake.data.source.request

data class AddItemRequest(
    val name: String,
    val desc: String,
    val category: String,
    val latitude: String,
    val longitude: String,
    val maxRadius: String,
    val status: Int
)