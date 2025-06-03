package com.exam.eventradar.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey val id: String,
    val name: String,
    val date: String,
    val location: String,
    val imageUrl: String?,
    val description: String?,
    val externalLink: String?,
    val contacts: String?,
    val isFavorite: Boolean = false
)
