package com.vincent.givetake.ui.activity.map

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AddressResult(
    val latlang: LatLng?,
    val name: String?,
    val address: String?,
) : Parcelable
