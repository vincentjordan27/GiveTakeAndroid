package com.vincent.givetake.ui.fragment.chat

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserChat(
    val userName: String = "",
    val token: String = "",
    val id: String = "",
    val itemName: String
) : Parcelable
