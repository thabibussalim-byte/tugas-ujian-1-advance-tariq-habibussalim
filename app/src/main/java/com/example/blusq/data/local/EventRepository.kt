package com.example.blusq.data.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.example.blusq.data.local.entity.EventEntity
import com.example.blusq.data.local.room.EventDao
import com.example.blusq.data.remote.response.ListEventsItem
import com.example.blusq.data.remote.retrofit.ApiService
import kotlinx.coroutines.Dispatchers

class EventRepository private constructor(
    private val apiService: ApiService,
    private val eventDao: EventDao
)
{
    fun getUpcomingEvents(): LiveData<Result<List<EventEntity>>> = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val response = apiService.getUpcomingEvents()
            val events = response.listEvents
            val eventList = events?.filterNotNull()?.map {
                val isFavorite = eventDao.isEventFavorite(it.id ?: 0)
                EventEntity(
                    id = it.id ?: 0,
                    name = it.name,
                    summary = it.summary,
                    ownerName = it.ownerName,
                    cityName = it.cityName,
                    beginTime = it.beginTime,
                    endTime = it.endTime,
                    category = it.category,
                    imageLogo = it.imageLogo,
                    mediaCover = it.mediaCover,
                    link = it.link,
                    description = it.description,
                    registrants = it.registrants,
                    quota = it.quota,
                    isFavorite = isFavorite,
                    isFinished = false

                )
            } ?: emptyList()
            eventDao.deleteAllNonFavorites(false)
            eventDao.insertEvents(eventList)
        } catch (e: Exception) {

            emit(Result.Error(e.message.toString()))
        }
        val localData: LiveData<Result<List<EventEntity>>> = eventDao.getUpcomingEvents().map { Result.Success(it) }
        emitSource(localData)
    }



    fun getFinishedEvents(): LiveData<Result<List<EventEntity>>> = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val response = apiService.getFinishedEvents()
            val events = response.listEvents
            val eventList = events?.filterNotNull()?.map {
                val isFavorite = eventDao.isEventFavorite(it.id ?: 0)
                EventEntity(
                    id = it.id ?: 0,
                    name = it.name,
                    summary = it.summary,
                    ownerName = it.ownerName,
                    cityName = it.cityName,
                    beginTime = it.beginTime,
                    endTime = it.endTime,
                    category = it.category,
                    imageLogo = it.imageLogo,
                    mediaCover = it.mediaCover,
                    link = it.link,
                    description = it.description,
                    registrants = it.registrants,
                    quota = it.quota,
                    isFavorite = isFavorite,
                    isFinished = true
                )
            } ?: emptyList()

            eventDao.deleteAllNonFavorites(true)
            eventDao.insertEvents(eventList)

        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
        val localData: LiveData<Result<List<EventEntity>>> = eventDao.getFinishedEvents().map { Result.Success(it) }
        emitSource(localData)
    }


    fun getEventById(id: Int): LiveData<EventEntity> {
        return eventDao.getEventById(id)
    }

    fun getFavoriteEvents(): LiveData<List<EventEntity>> {
        return eventDao.getFavoriteEvents()
    }

    suspend fun setFavoriteEvent(id: Int, favoriteState: Boolean) {
        val event = eventDao.getEventByIdSync(id)
        if (event != null) {
            event.isFavorite = favoriteState
            eventDao.updateEvent(event)
        }
    }

    suspend fun getLatestEvent(): ListEventsItem? {
        return try {
            val response = apiService.getLatestEvent()
            response.listEvents?.firstOrNull()
        } catch (_: Exception) {
            null
        }
    }

    fun searchEvents(query: String): LiveData<Result<List<EventEntity>>> = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val response = apiService.searchEvents(query = query)
            val events = response.listEvents
            val eventList = events?.filterNotNull()?.map {
                EventEntity(
                    id = it.id ?: 0,
                    name = it.name,
                    summary = it.summary,
                    ownerName = it.ownerName,
                    cityName = it.cityName,
                    beginTime = it.beginTime,
                    endTime = it.endTime,
                    category = it.category,
                    imageLogo = it.imageLogo,
                    mediaCover = it.mediaCover,
                    link = it.link,
                    description = it.description,
                    registrants = it.registrants,
                    quota = it.quota,
                    isFavorite = false,
                    isFinished = false
                )
            } ?: emptyList()
            emit(Result.Success(eventList))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

            companion object {
        @Volatile
        private var instance: EventRepository? = null

        fun getInstance(
            apiService: ApiService,
            eventDao: EventDao
        ): EventRepository =
            instance ?: synchronized(this) {
                instance ?: EventRepository(apiService, eventDao)
            }.also { instance = it }
    }
}