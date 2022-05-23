package com.vincent.givetake.data.source.response.items

data class ReceiverListResponse(
    val status: String,
    val data: List<ReceiverItem>,
    val message: String
)

data class ReceiverItem(
    val status: Int,
    val requestId: String,
    val distance: String,
    val name: String,
    val reason: String,
    val photo: String
)
