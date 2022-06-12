package com.vincent.givetake.ui.activity.map

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MapDirectionData(
    val from: LatLng,
    val to: LatLng,
    val addressFrom: String,
    val addressTo: String
) : Parcelable
