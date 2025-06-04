package com.exam.eventradar.data.local.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.exam.eventradar.data.local.entities.EventEntity

@Dao
interface EventDao {
    @Query("SELECT * FROM events")
    fun getAllEvents(): LiveData<List<EventEntity>>

    @Query("SELECT * FROM events")
    suspend fun getAllEventsSync(): List<EventEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<EventEntity>)

    @Query("SELECT * FROM events WHERE isFavorite = 1")
    fun getFavoriteEvents(): LiveData<List<EventEntity>>

    @Query("UPDATE events SET isFavorite = :isFavorite WHERE id = :eventId")
    suspend fun updateFavoriteStatus(eventId: String, isFavorite: Boolean)

    @Query("DELETE FROM events WHERE TRIM(location) = :city")
    suspend fun deleteEventsByCity(city: String)

    @Query("SELECT * FROM events WHERE TRIM(location) = :city AND date = :date")
    suspend fun searchEvents(city: String, date: String): List<EventEntity>

    @Query("SELECT * FROM events WHERE TRIM(location) = :city")
    suspend fun getEventsByCity(city: String): List<EventEntity>
}
