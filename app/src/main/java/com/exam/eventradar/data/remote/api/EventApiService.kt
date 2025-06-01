package com.exam.eventradar.data.remote.api

import com.exam.eventradar.data.remote.model.EventResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface EventApiService {
    @GET("get_events.php")
    suspend fun getEvents(
        @Query("city") city: String,
        @Query("date") date: String
    ): EventResponse

    @GET("get_all_events.php")
    suspend fun getAllEvents(): EventResponse

    @GET("eventsByCity.php")
    suspend fun getEventsByCity(
        @Query("city") city: String
    ): EventResponse

}
