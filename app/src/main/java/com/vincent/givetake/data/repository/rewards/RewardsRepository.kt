package com.vincent.givetake.data.repository.rewards

import com.google.gson.Gson
import com.vincent.givetake.data.source.api.RewardsService
import com.vincent.givetake.data.source.request.RedeemRequest
import com.vincent.givetake.data.source.response.items.DetailResponseLogin
import com.vincent.givetake.data.source.response.items.StatusResponse
import com.vincent.givetake.data.source.response.rewards.RewardDetail
import com.vincent.givetake.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class RewardsRepository(private val apiService: RewardsService) {

    fun getRewards() = flow {
        emit(Result.Loading)
        emit(Result.Success(apiService.getRewards()))
    }.catch { emit(Result.Error("Server timeout. Silahkan dicoba kembali beberapa saat lagi"))
    }.flowOn(Dispatchers.IO)

    fun getMyRewards(token: String) = flow {
        emit(Result.Loading)
        emit(Result.Success(apiService.getMyRewards(token)))
    }.catch { emit(Result.Error("Server timeout. Silahkan dicoba kembali beberapa saat lagi"))
    }.flowOn(Dispatchers.IO)

    fun getRewardById(id: String, rewardId: String) = flow {
        emit(Result.Loading)
        val response = apiService.getRewardById(id, rewardId)
        if (response.isSuccessful) {
            emit(Result.Success(response.body()))
        } else {
            val errorResponse = Gson().fromJson(response.errorBody()!!.string(), RewardDetail::class.java)
            emit(Result.Error(errorResponse.message))
        }
    }.catch { emit(Result.Error("Server timeout. Silahkan dicoba kembali beberapa saat lagi"))
    }.flowOn(Dispatchers.IO)

    fun redeemReward(id: String, rewardId: String, body: RedeemRequest) = flow {
        emit(Result.Loading)
        val response = apiService.redeemReward(id, rewardId, body)
        if (response.isSuccessful) {
            emit(Result.Success(response.body()))
        } else {
            val errorResponse = Gson().fromJson(response.errorBody()!!.string(), StatusResponse::class.java)
            emit(Result.Error(errorResponse.message))
        }
    }.catch { emit(Result.Error("Server timeout. Silahkan dicoba kembali beberapa saat lagi"))
    }.flowOn(Dispatchers.IO)

    companion object {

        @Volatile
        private var instance: RewardsRepository? = null

        fun getInstance(
            apiService: RewardsService,
        ) : RewardsRepository {
            return instance ?: synchronized(this) {
                RewardsRepository(apiService).also {
                    instance = it
                }
            }
        }
    }

}