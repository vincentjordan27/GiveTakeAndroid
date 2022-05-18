package com.vincent.givetake.data.source.response.items

import com.google.gson.annotations.SerializedName

data class MyItemsResponse(
    val status: String,
    val data: List<DataMyItem>,
    val message: String
)

data class DataMyItem(
    @field:SerializedName("ItemId")
    val id: String,
    val status: Int,
    val total: String,
    val thumbnail: String,
    val name: String
)