package com.vincent.givetake.data.source.response.rewards

data class MyRewardsResponse(
    val status: String,
    val data: List<MyRewardItem>
)

data class MyRewardItem(
    val id: String,
    val name: String,
    val date: String,
    val photo: String,
    val status: Int
)