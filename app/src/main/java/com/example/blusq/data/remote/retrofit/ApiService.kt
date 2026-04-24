package com.example.blusq.data.remote.retrofit

import com.example.blusq.data.remote.response.EventResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("events?active=1")
    suspend fun getUpcomingEvents(): EventResponse

    @GET("events?active=0")
    suspend fun getFinishedEvents(): EventResponse

    @GET("events")
    suspend fun searchEvents(
        @Query("active") active: Int = -1,
        @Query("q") query: String
    ): EventResponse

    @GET("events?active=-1&limit=1")
    suspend fun getLatestEvent(): EventResponse
}