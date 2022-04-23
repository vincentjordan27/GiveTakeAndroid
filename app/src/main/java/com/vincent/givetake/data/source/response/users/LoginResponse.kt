package com.vincent.givetake.data.source.response.users

data class LoginResponse(
    val status: String,
    val data: KeyLogin?,
    val message: String
)

data class KeyLogin(
    val userId: String,
    val accessToken: String,
)
