package com.exam.eventradar.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.exam.eventradar.data.local.db.dao.EventDao
import com.exam.eventradar.data.local.entities.EventEntity

@Database(entities = [EventEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "event_db"
                ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
            }
    }
}
