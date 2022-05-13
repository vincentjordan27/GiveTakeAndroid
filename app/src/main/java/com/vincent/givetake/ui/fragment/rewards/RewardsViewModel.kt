package com.vincent.givetake.ui.fragment.rewards

import androidx.lifecycle.*
import com.vincent.givetake.data.repository.rewards.RewardsRepository
import com.vincent.givetake.data.source.response.rewards.AllRewardsResponse
import com.vincent.givetake.data.source.response.rewards.MyRewardsResponse
import com.vincent.givetake.preference.UserPreferences
import com.vincent.givetake.utils.Result
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RewardsViewModel(private val repository: RewardsRepository, private val userPreferences: UserPreferences) : ViewModel() {
    val resultRewards = MutableLiveData<Result<AllRewardsResponse?>>()
    val resultMyReward = MutableLiveData<Result<MyRewardsResponse?>>()

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

}