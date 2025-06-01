package com.exam.eventradar.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.exam.eventradar.data.local.entities.EventEntity
import com.exam.eventradar.data.repository.EventRepository
import com.exam.eventradar.domain.models.Event
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel(private val repository: EventRepository) : ViewModel() {

    val events = repository.events

    // 🔹 Eventi correnti mostrati nella MainActivity
    private val _currentEvents = MutableLiveData<List<Event>>()
    val currentEvents: LiveData<List<Event>> = _currentEvents

    // 🔹 Preferiti
    val favoriteEvents: LiveData<List<EventEntity>> = repository.favoriteEvents

    // 🔹 Progress
    val progress = MutableLiveData<Boolean>()

    // 🔹 Messaggio di fallback
    private val _fallbackMessage = MutableLiveData<String>()
    val fallbackMessage: LiveData<String> = _fallbackMessage

    // 🔹 Carica TUTTI gli eventi (HOME)
    fun loadAllEvents(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            progress.postValue(true)
            repository.loadAllEvents()

            // Delay per permettere a Room di emettere i dati
            delay(300)

            _currentEvents.postValue(repository.events.value ?: emptyList())
            progress.postValue(false)

            // Notifica che ha finito
            onComplete()
        }
    }

    // 🔹 Ricerca con fallback
    fun refreshEventsOrFallback(city: String, date: String) {
        viewModelScope.launch {
            progress.postValue(true)

            val searchResult = repository.searchEvents(city, date)

            if (searchResult.isNotEmpty()) {
                // Caso: eventi trovati
                _currentEvents.postValue(searchResult)
                _fallbackMessage.postValue("") // reset messaggio
            } else {
                // Caso fallback
                _fallbackMessage.postValue("Nessun evento in programma in data $date, ecco altri eventi a $city")

                repository.refreshEventsByCity(city)

                delay(300)

                val fallbackList = repository.getEventsByCity(city)
                _currentEvents.postValue(fallbackList)
            }

            progress.postValue(false)
        }
    }

    // 🔹 Set preferito
    fun setFavoriteStatus(eventId: String, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.setFavoriteStatus(eventId, isFavorite)
        }
    }

    // 🔹 Metodo base se vuoi ancora chiamare refresh semplice (opzionale)
    fun refreshEvents(city: String, date: String) {
        viewModelScope.launch {
            repository.refreshEventsFromCityAndDate(city, date)
            _currentEvents.postValue(repository.events.value ?: emptyList())
        }
    }

    fun setCurrentEvents(events: List<Event>) {
        _currentEvents.postValue(events)
    }

}

class MainViewModelFactory(private val repository: EventRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
