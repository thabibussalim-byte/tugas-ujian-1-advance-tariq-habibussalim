package com.example.blusq.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.blusq.data.local.EventRepository
import com.example.blusq.data.local.entity.EventEntity
import com.example.blusq.ui.SettingPreferences
import kotlinx.coroutines.launch

class MainViewModel(private val pref: SettingPreferences, private val eventRepository: EventRepository) : ViewModel() {

    fun getThemeSettings(): LiveData<Boolean> {
        return pref.getThemeSetting().asLiveData()
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            pref.saveThemeSetting(isDarkModeActive)
        }
    }

    fun getUpcomingEvents() = eventRepository.getUpcomingEvents()

    fun getFinishedEvents() = eventRepository.getFinishedEvents()

    fun getFavoriteEvents() = eventRepository.getFavoriteEvents()

    fun getEventById(id: Int): LiveData<EventEntity> {
        return eventRepository.getEventById(id)
    }


    fun setFavoriteEvent(id: Int, favoriteState: Boolean) {
        viewModelScope.launch {
            eventRepository.setFavoriteEvent(id, favoriteState)
        }
    }

}