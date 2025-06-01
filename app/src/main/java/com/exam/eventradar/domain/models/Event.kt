package com.exam.eventradar.domain.models

data class Event(
    val id: String,
    val name: String,
    val date: String,
    val location: String,
    val imageUrl: String
)
