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
                imageUrl = entity.imageUrl
            )
        }
    }

    suspend fun refreshEventsFromCityAndDate(city: String, date: String) {
        Log.d("API_DEBUG", "Cerco eventi per $city - $date")
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
                    isFavorite = old?.isFavorite ?: false // ✅ preserva lo stato
                )
            }

            // ✅ Inserisce senza cancellare (replace mantiene preferiti)
            eventDao.insertEvents(entities)

            Log.d("API", "Eventi ricevuti: ${entities.size}")
        } catch (e: Exception) {
            Log.e("API_DEBUG", "Errore durante la chiamata Retrofit", e)
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
                    isFavorite = old?.isFavorite ?: false // ✅ anche qui preserva
                )
            }

            eventDao.insertEvents(entities)

            Log.d("API", "Caricati tutti gli eventi: ${entities.size}")
        } catch (e: Exception) {
            Log.e("API", "Errore caricamento eventi generali: ${e.localizedMessage}")
        }
    }

//    suspend fun saveEvent(event: Event) {
//        val existing = eventDao.getEventById(event.id)
//        val updated = EventEntity(
//            id = event.id,
//            name = event.name,
//            date = event.date,
//            location = event.location,
//            imageUrl = event.imageUrl,
//            isFavorite = existing?.isFavorite ?: false
//        )
//        eventDao.insertEvents(listOf(updated))
//    }

    suspend fun setFavoriteStatus(eventId: String, isFavorite: Boolean) {
        eventDao.updateFavoriteStatus(eventId, isFavorite)
    }

    val favoriteEvents: LiveData<List<EventEntity>> = eventDao.getFavoriteEvents()

    suspend fun refreshEventsByCity(city: String) {
        try {
            val response = apiService.getEventsByCity(city)

            // Rimuovi TUTTI gli eventi della città corrente PRIMA di inserire
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
                imageUrl = it.imageUrl
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
                imageUrl = entity.imageUrl
            )
        }
    }


}
