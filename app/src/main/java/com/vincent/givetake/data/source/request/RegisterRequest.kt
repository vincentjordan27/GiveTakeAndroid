package com.vincent.givetake.data.source.request

data class RegisterRequest(
    val name: String,
    val email: String,
    val username: String,
    val password: String,
    val latitude: String,
    val longitude: String,
    val phone: String,
    val address: String,
)