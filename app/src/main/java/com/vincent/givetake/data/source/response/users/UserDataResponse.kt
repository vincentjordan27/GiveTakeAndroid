package com.vincent.givetake.data.source.response.users

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class UserDataResponse(
    val status: String,
    val data: UserData,
    val message: String
)

@Parcelize
data class UserData(
    val id: String,
    val name: String,
    val point: Int,
    val email: String,
    val phone: String,
    val address: String,
    val photo: String,
    val longitude: String,
    val latitude: String,
    var accessKey: String = ""
) : Parcelable