package com.vincent.givetake.data.source.response.rewards

import com.google.gson.annotations.SerializedName

data class AllRewardsResponse(
    val status: String,
    val data: List<RewardItem>
)

data class RewardItem(
    val id: String,
    val name: String,
    val photo: String,
    @field:SerializedName("desc_reward")
    val desc: String,
    val price: Int,
    val stock: Int,
)
