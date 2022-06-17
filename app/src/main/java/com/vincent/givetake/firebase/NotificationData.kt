package com.vincent.givetake.firebase

data class NotificationData(
    val data: DataNotification,
    val registration_ids: List<String>
)

data class DataNotification(
    val fcmType: String,
    val fcmChatName: String = "",
    val fcmItemName: String = ""
)
