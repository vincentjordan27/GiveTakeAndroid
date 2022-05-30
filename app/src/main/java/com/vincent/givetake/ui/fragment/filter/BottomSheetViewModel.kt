package com.vincent.givetake.ui.fragment.filter

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.vincent.givetake.preference.FilterData
import com.vincent.givetake.preference.UserPreferences
import kotlinx.coroutines.launch

class BottomSheetViewModel(private val preferences: UserPreferences): ViewModel() {
    fun reset() = viewModelScope.launch{
        preferences.reset()
    }

    fun saveFilter(data: FilterData) = viewModelScope.launch {
        preferences.saveFilterSearch(data)
    }
}