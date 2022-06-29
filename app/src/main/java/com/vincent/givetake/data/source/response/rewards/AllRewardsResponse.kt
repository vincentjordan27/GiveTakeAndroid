package com.vincent.givetake.data.source.response.rewards

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

data class AllRewardsResponse(
    val status: String,
    val data: List<RewardItem>
)

@Parcelize
data class RewardItem(
    val id: String,
    val name: String,
    val photo: String,
    @field:SerializedName("desc_reward")
    val desc: String,
    val price: Int,
    val stock: Int,
) : Parcelable

data class RewardDetail(
    val status: String,
    val data: RewardItem,
    val valid: Boolean,
    val message: String,
    val point: Int
)
