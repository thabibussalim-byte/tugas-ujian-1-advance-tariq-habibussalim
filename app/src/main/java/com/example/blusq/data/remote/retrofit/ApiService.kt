package com.example.blusq.data.remote.retrofit

import com.example.blusq.data.remote.response.DetailEventResponse
import com.example.blusq.data.remote.response.EventResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("events?active=1")
    suspend fun getUpcomingEvents(): EventResponse

    @GET("events?active=0")
    suspend fun getFinishedEvents(): EventResponse

    @GET("events")
    suspend fun searchEvents(
        @Query("q") keyword: String,
        @Query("active") active: Int
    ): EventResponse


    @GET("events/{id}")
    suspend fun getDetailEvent(
        @Path("id") id: String
    ): DetailEventResponse
}