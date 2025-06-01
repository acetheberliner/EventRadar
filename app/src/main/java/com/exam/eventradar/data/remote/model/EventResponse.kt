package com.exam.eventradar.data.remote.model

import com.exam.eventradar.domain.models.Event

data class EventResponse(
    val events: List<Event>
)
