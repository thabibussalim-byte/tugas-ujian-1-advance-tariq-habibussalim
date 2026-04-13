package com.example.blusq.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.blusq.data.local.EventRepository
import com.example.blusq.di.Injection
import com.example.blusq.ui.main.MainViewModel

class ViewModelFactory(private val pref: SettingPreferences, private val eventRepository: EventRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(pref,eventRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: ViewModelFactory(Injection.providePreferences(context),Injection.provideRepository(context))
                    .also { INSTANCE = it }
            }
    }
}