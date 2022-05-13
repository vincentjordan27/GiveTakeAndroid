package com.vincent.givetake.data.source.api

import com.vincent.givetake.data.source.response.rewards.AllRewardsResponse
import com.vincent.givetake.data.source.response.rewards.MyRewardsResponse
import retrofit2.http.GET
import retrofit2.http.Header

interface RewardsService {
    @GET("rewards")
    suspend fun getRewards() : AllRewardsResponse

    @GET("myrewards")
    suspend fun getMyRewards(
        @Header("Authorization") auth: String,
    ) : MyRewardsResponse
}