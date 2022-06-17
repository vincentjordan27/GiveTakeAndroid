package com.vincent.givetake.data.source.response.chat

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class AllChatListResponse(
    val status: String,
    val data: List<ChatItemResponse>,
    val message: String
)

@Parcelize
data class ChatItemResponse(
    @field:SerializedName("chat_id")
    val chatId: String,
    @field:SerializedName("sender_id")
    val senderId: String,
    @field:SerializedName("receiver_id")
    val receiverId: String,
    @field:SerializedName("item_id")
    val itemId: String,
    @field:SerializedName("ishasmessage")
    val valid: Int,
    @field:SerializedName("sender_name")
    val senderName: String,
    @field:SerializedName("receiver_name")
    val receiverName: String,
    @field:SerializedName("sender_token")
    val senderToken: String,
    @field:SerializedName("receiver_token")
    val receiverToken: String,
    val name: String,
) : Parcelable