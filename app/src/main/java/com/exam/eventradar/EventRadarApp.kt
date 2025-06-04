package com.exam.eventradar

import android.app.Application
import com.exam.eventradar.data.local.db.AppDatabase
import com.exam.eventradar.data.remote.api.EventApiService
import com.exam.eventradar.data.repository.EventRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class EventRadarApp : Application() {

    lateinit var repository: EventRepository
        private set

    override fun onCreate() {
        super.onCreate()

        val database = AppDatabase.getDatabase(this)

        val apiService = Retrofit.Builder()
            .baseUrl("http://192.168.178.131/eventradar_api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EventApiService::class.java)

        repository = EventRepository(apiService, database.eventDao())
    }
}
