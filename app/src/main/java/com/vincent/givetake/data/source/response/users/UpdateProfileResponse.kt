package com.vincent.givetake.data.source.response.users

data class UpdateUserResponse(
    val status: String,
    val data: DataUserId,
    val message: String
)

data class DataUserId(
    val id: String
)