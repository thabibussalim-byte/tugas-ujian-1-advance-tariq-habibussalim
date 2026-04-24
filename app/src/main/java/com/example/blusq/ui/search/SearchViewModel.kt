package com.example.blusq.ui.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.example.blusq.data.local.EventRepository

class SearchViewModel(private val eventRepository: EventRepository) : ViewModel() {
    private val _query = MutableLiveData<String>()
    
    val searchResult = _query.switchMap { query ->
        eventRepository.searchEvents(query)
    }

    fun setQuery(query: String) {
        if (_query.value == query) return
        _query.value = query
    }
}