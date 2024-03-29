package com.vincent.givetake.di

import android.content.Context
import androidx.datastore.core.DataStore
import com.vincent.givetake.data.repository.advice.AdviceRepository
import com.vincent.givetake.data.repository.chat.ChatRepository
import com.vincent.givetake.data.repository.email.EmailRepository
import com.vincent.givetake.data.repository.items.ItemsRepository
import com.vincent.givetake.data.repository.otp.OtpRepository
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

    fun provideChatRepository(): ChatRepository {
        val apiService = ApiClient.getChatService()
        return ChatRepository.getInstance(apiService)
    }

    fun provideAdviceRepository(): AdviceRepository {
        val apiService = ApiClient.getAdviceService()
        return AdviceRepository.getInstance(apiService)
    }

    fun provideOtpRepository(): OtpRepository {
        val apiService = ApiClient.getOtpService()
        return OtpRepository.getInstance(apiService)
    }

    fun provideEmailRepository(): EmailRepository {
        val apiService = ApiClient.getEmailService()
        return EmailRepository.getInstance(apiService)
    }
}