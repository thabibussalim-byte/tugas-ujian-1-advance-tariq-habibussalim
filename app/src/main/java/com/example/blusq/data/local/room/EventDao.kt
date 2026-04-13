package com.example.blusq.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.blusq.data.local.entity.EventEntity

@Dao
interface EventDao {

    @Query("SELECT * FROM event WHERE isFinished = 0 ORDER BY beginTime DESC")
    fun getUpcomingEvents(): LiveData<List<EventEntity>>

    @Query("SELECT * FROM event WHERE isFinished = 1 ORDER BY beginTime DESC")
    fun getFinishedEvents(): LiveData<List<EventEntity>>

    @Query("SELECT * FROM event WHERE isFavorite = 1")
    fun getFavoriteEvents(): LiveData<List<EventEntity>>

    @Update
    suspend fun updateEvent(event: EventEntity)

    @Query("DELETE FROM event WHERE isFavorite = 0 AND isFinished = :isFinished")
    suspend fun deleteAllNonFavorites(isFinished: Boolean)

    @Query("SELECT EXISTS(SELECT * FROM event WHERE id = :id AND isFavorite = 1)")
    suspend fun isEventFavorite(id: Int): Boolean

    @Query("SELECT * FROM event WHERE id = :id")
    fun getEventById(id: Int): LiveData<EventEntity>

   @Query("SELECT * FROM event WHERE id = :id")
    suspend fun getEventByIdSync(id: Int): EventEntity?

    @Query("SELECT * FROM event")
    fun getAllEvents(): LiveData<List<EventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<EventEntity>)

    @Query("SELECT * FROM event WHERE name LIKE '%' || :keyword || '%'")
    fun searchEvents(keyword: String): LiveData<List<EventEntity>>
}