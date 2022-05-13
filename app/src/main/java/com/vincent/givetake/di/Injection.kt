package com.vincent.givetake.di

import android.content.Context
import androidx.datastore.core.DataStore
import com.vincent.givetake.data.repository.items.ItemsRepository
import com.vincent.givetake.data.repository.rewards.RewardsRepository
import com.vincent.givetake.data.repository.users.UsersRepository
import com.vincent.givetake.data.source.ApiClient


object Injection {
    fun provideItemsRepository() : ItemsRepository {
        val apiService = ApiClient.getItemsService()
        return ItemsRepository.getInstance(apiService)
    }

    fun provideUsersRepository() : UsersRepository {
        val apiService = ApiClient.getUsersService()
        return UsersRepository.getInstance(apiService)
    }

    fun provideRewardsRepository() : RewardsRepository {
        val apiService = ApiClient.getRewardsService()
        return RewardsRepository.getInstance(apiService)
    }
}