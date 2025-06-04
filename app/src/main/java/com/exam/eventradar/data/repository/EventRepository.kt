package com.exam.eventradar.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.exam.eventradar.data.local.db.dao.EventDao
import com.exam.eventradar.data.local.entities.EventEntity
import com.exam.eventradar.data.remote.api.EventApiService
import com.exam.eventradar.domain.models.Event

class EventRepository(
    private val apiService: EventApiService,
    private val eventDao: EventDao
) {
    val events: LiveData<List<Event>> = eventDao.getAllEvents().map { list ->
        list.map { entity ->
            Event(
                id = entity.id,
                name = entity.name,
                date = entity.date,
                location = entity.location,
                imageUrl = entity.imageUrl,
                description = entity.description,
                externalLink = entity.externalLink,
                contacts = entity.contacts
            )
        }
    }

    suspend fun refreshEventsFromCityAndDate(city: String, date: String) {
        try {
            val response = apiService.getEvents(city, date)
            val existing = eventDao.getAllEventsSync().associateBy { it.id }

            val entities = response.events.map { event ->
                val old = existing[event.id]
                EventEntity(
                    id = event.id,
                    name = event.name,
                    date = event.date,
                    location = event.location,
                    imageUrl = event.imageUrl,
                    description = event.description,
                    externalLink = event.externalLink,
                    contacts = event.contacts,
                    isFavorite = old?.isFavorite ?: false
                )
            }

            eventDao.insertEvents(entities)

            Log.d("API", "Eventi ricevuti: ${entities.size}")
        } catch (e: Exception) {
            Log.e("API", "Errore rete: ${e.localizedMessage}")
        }
    }

    suspend fun loadAllEvents() {
        try {
            val response = apiService.getAllEvents()
            val existing = eventDao.getAllEventsSync().associateBy { it.id }

            val entities = response.events.map { event ->
                val old = existing[event.id]
                EventEntity(
                    id = event.id,
                    name = event.name,
                    date = event.date,
                    location = event.location,
                    imageUrl = event.imageUrl,
                    description = event.description,
                    externalLink = event.externalLink,
                    contacts = event.contacts,
                    isFavorite = old?.isFavorite ?: false
                )
            }

            eventDao.insertEvents(entities)

            Log.d("API", "Caricati tutti gli eventi: ${entities.size}")
        } catch (e: Exception) {
            Log.e("API", "Errore caricamento eventi generali: ${e.localizedMessage}")
        }
    }

    suspend fun setFavoriteStatus(eventId: String, isFavorite: Boolean) {
        eventDao.updateFavoriteStatus(eventId, isFavorite)
    }

    val favoriteEvents: LiveData<List<EventEntity>> = eventDao.getFavoriteEvents()

    suspend fun refreshEventsByCity(city: String) {
        try {
            val response = apiService.getEventsByCity(city)

            eventDao.deleteEventsByCity(city)

            val existing = eventDao.getAllEventsSync().associateBy { it.id }

            val entities = response.events.map { event ->
                val old = existing[event.id]
                EventEntity(
                    id = event.id,
                    name = event.name,
                    date = event.date,
                    location = event.location,
                    imageUrl = event.imageUrl,
                    description = event.description,
                    externalLink = event.externalLink,
                    contacts = event.contacts,
                    isFavorite = old?.isFavorite ?: false
                )
            }

            eventDao.insertEvents(entities)

            Log.d("API", "Fallback eventi città $city: ${entities.size}")
        } catch (e: Exception) {
            Log.e("API", "Errore fallback eventi città: ${e.localizedMessage}")
        }
    }

    suspend fun searchEvents(city: String, date: String): List<Event> {
        val result = eventDao.searchEvents(city, date)
        return result.map {
            Event(
                id = it.id,
                name = it.name,
                date = it.date,
                location = it.location,
                imageUrl = it.imageUrl,
                description = it.description,
                externalLink = it.externalLink,
                contacts = it.contacts
            )
        }
    }

    suspend fun getEventsByCity(city: String): List<Event> {
        val entities = eventDao.getEventsByCity(city)
        return entities.map { entity ->
            Event(
                id = entity.id,
                name = entity.name,
                date = entity.date,
                location = entity.location,
                imageUrl = entity.imageUrl,
                description = entity.description,
                externalLink = entity.externalLink,
                contacts = entity.contacts
            )
        }
    }
}