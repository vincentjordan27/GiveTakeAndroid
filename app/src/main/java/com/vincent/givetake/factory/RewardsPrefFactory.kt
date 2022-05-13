package com.vincent.givetake.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vincent.givetake.data.repository.rewards.RewardsRepository
import com.vincent.givetake.di.Injection
import com.vincent.givetake.preference.UserPreferences
import com.vincent.givetake.ui.fragment.rewards.RewardsViewModel

class RewardsPrefFactory(private val rewardsRepository: RewardsRepository, private val userPreferences: UserPreferences): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RewardsViewModel::class.java)) {
            return RewardsViewModel(rewardsRepository, userPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: RewardsPrefFactory? = null

        fun getInstance(pref: UserPreferences): RewardsPrefFactory =
            instance ?: synchronized(this) {
                instance ?: RewardsPrefFactory(Injection.provideRewardsRepository(), pref)
            }.also { instance = it }
    }
}