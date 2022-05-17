package com.vincent.givetake.data.source.response.items

import com.google.gson.annotations.SerializedName

data class DetailResponseLogin(
    val status: String,
    val data: DataDetailResponseLogin,
    val message: String
)

data class DataDetailResponseLogin(
    val request: Int,
    val wish: Boolean,
    val items: List<ListItemDetailResponseLogin>,
    val images: List<ImageItemDetail>
)

data class ImageItemDetail(
    val id: String,
    val url: String,
)

data class ListItemDetailResponseLogin(
    val id: String,
    val name: String,
    @SerializedName("description")
    val desc: String,
    val category: String,
    @SerializedName("user_id")
    val userId: String,
    val latitude: String,
    val longitude: String,
    @SerializedName("max_radius")
    val maxRadius: String,
    @SerializedName("status")
    val itemStatus: Int,
    val address: String,
    val ulasan: String = ""
)