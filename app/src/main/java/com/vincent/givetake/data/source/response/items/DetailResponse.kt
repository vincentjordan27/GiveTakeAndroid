package com.vincent.givetake.data.source.response.items

import com.google.gson.annotations.SerializedName

data class DetailResponseNonLogin(
    val status: String,
    val data: DataDetailResponseNonLogin,
    val message: String
)

data class DataDetailResponseNonLogin(
    val request: Int,
    val wish: Boolean,
    val items: List<ListItemResponseNonLogin>,
    val images: List<ImageItemDetail>
)

data class ListItemResponseNonLogin(
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
    val ulasan: String = ""
)

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
    val ulasan: String = ""
)