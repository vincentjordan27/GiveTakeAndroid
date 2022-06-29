package com.vincent.givetake.data.source.api

import com.vincent.givetake.data.source.request.RedeemRequest
import com.vincent.givetake.data.source.response.items.StatusResponse
import com.vincent.givetake.data.source.response.rewards.AllRewardsResponse
import com.vincent.givetake.data.source.response.rewards.MyRewardsResponse
import com.vincent.givetake.data.source.response.rewards.RewardDetail
import retrofit2.Response
import retrofit2.http.*

interface RewardsService {
    @GET("rewards")
    suspend fun getRewards() : AllRewardsResponse

    @GET("myrewards")
    suspend fun getMyRewards(
        @Header("Authorization") auth: String,
    ) : MyRewardsResponse

    @GET("reward/{id}")
    suspend fun getRewardById(
        @Header("Authorization") auth: String,
        @Path("id") id: String
    ) : Response<RewardDetail>

    @POST("redeem/{id}")
    suspend fun redeemReward(
        @Header("Authorization") auth: String,
        @Path("id") rewardId: String,
        @Body body: RedeemRequest
    ) : Response<StatusResponse>
}