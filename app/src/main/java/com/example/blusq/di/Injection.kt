package com.example.blusq.di

import android.content.Context
import com.example.blusq.data.local.EventRepository
import com.example.blusq.data.local.room.EventDatabase
import com.example.blusq.data.remote.retrofit.ApiConfig
import com.example.blusq.ui.SettingPreferences
import com.example.blusq.ui.dataStore

object Injection {
    fun provideRepository(context: Context): EventRepository {
        val apiService = ApiConfig.getApiService()
        val database = EventDatabase.getInstance(context)
        val dao = database.eventDao()
        return EventRepository.getInstance(apiService, dao)
    }

    fun providePreferences(context: Context): SettingPreferences {
        return SettingPreferences.getInstance(context.dataStore)
    }
}