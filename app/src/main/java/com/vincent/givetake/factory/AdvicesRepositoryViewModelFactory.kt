package com.vincent.givetake.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vincent.givetake.data.repository.advice.AdviceRepository
import com.vincent.givetake.di.Injection
import com.vincent.givetake.ui.activity.advice.AdviceViewModel
import com.vincent.givetake.ui.activity.items.add.viewmodel.AddViewModel
import com.vincent.givetake.ui.activity.detail.DetailViewModel
import com.vincent.givetake.ui.activity.receive.ReceiveViewModel
import com.vincent.givetake.ui.activity.receiver.ReceiverViewModel
import com.vincent.givetake.ui.activity.request.RequestViewModel

class AdvicesRepositoryViewModelFactory(private val adviceRepository: AdviceRepository): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdviceViewModel::class.java)) {
            return AdviceViewModel(adviceRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: AdvicesRepositoryViewModelFactory? = null

        fun getInstance(): AdvicesRepositoryViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: AdvicesRepositoryViewModelFactory(Injection.provideAdviceRepository())
            }.also { instance = it }
    }
}