package com.vincent.givetake.ui.fragment.rewards

import androidx.lifecycle.*
import com.vincent.givetake.data.repository.rewards.RewardsRepository
import com.vincent.givetake.data.source.request.RedeemRequest
import com.vincent.givetake.data.source.response.items.StatusResponse
import com.vincent.givetake.data.source.response.rewards.AllRewardsResponse
import com.vincent.givetake.data.source.response.rewards.MyRewardsResponse
import com.vincent.givetake.data.source.response.rewards.RewardDetail
import com.vincent.givetake.preference.UserPreferences
import com.vincent.givetake.utils.Result
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RewardsViewModel(private val repository: RewardsRepository, private val userPreferences: UserPreferences) : ViewModel() {
    val resultRewards = MutableLiveData<Result<AllRewardsResponse?>>()
    val resultMyReward = MutableLiveData<Result<MyRewardsResponse?>>()
    val resultDetailReward = MutableLiveData<Result<RewardDetail?>>()
    val resultRedeemReward = MutableLiveData<Result<StatusResponse?>>()

    fun getAllRewards() = viewModelScope.launch {
        repository.getRewards().collect {
            resultRewards.value = it
        }
    }

    fun getMyRewards(token: String) = viewModelScope.launch {
        repository.getMyRewards(token).collect {
            resultMyReward.value = it
        }
    }

    fun getAccessKey() : LiveData<String> {
        return userPreferences.getUserAccessKey().asLiveData()
    }

    fun getRewardById(id: String, rewardId: String) = viewModelScope.launch {
        repository.getRewardById(id, rewardId).collect {
            resultDetailReward.value = it
        }
    }

    fun redeemReward(id: String, rewardId: String, body: RedeemRequest) = viewModelScope.launch {
        repository.redeemReward(id, rewardId, body).collect {
            resultRedeemReward.value = it
        }
    }

}