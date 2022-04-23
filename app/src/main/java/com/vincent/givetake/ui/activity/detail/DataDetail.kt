package com.vincent.givetake.ui.activity.detail

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DataDetail(
    val role: String,
    val itemId: String,
    val radius: String?,
    val distance: String?,
    val userId: String = "",
    val accessKey: String = ""
) : Parcelable
