package com.vincent.givetake.data.source.response.items

data class AllItemResponse (
    val status: String,
    val data: ArrayList<ItemResponse>
    )

data class ItemResponse(
    val id: String,
    val name: String,
    val radius: String?,
    val userId: String,
    val maxRadius: String,
    val distance: String?,
)