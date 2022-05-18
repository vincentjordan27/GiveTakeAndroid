package com.vincent.givetake.data.source.response.items

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class MyOffersResponse(
    val status: String,
    val data: List<DataOffer>,
    val message: String
)

@Parcelize
data class DataOffer(
    val id: String,
    @field:SerializedName("user_id")
    val userId: String,
    @field:SerializedName("item_id")
    val itemId: String,
    val status: Int,
    val thumbnail: String,
    val name: String
) : Parcelable

