package com.vincent.givetake.data.repository.rewards

import com.vincent.givetake.data.source.api.RewardsService
import com.vincent.givetake.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class RewardsRepository(private val apiService: RewardsService) {

    fun getRewards() = flow {
        emit(Result.Loading)
        emit(Result.Success(apiService.getRewards()))
    }.catch { emit(Result.Error(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    fun getMyRewards(token: String) = flow {
        emit(Result.Loading)
        emit(Result.Success(apiService.getMyRewards(token)))
    }.catch { emit(Result.Error(it.message.toString()))
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