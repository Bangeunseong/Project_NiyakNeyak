package com.capstone.project_niyakneyak.data.alarm_resource

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import com.capstone.project_niyakneyak.data.alarm_model.Alarm
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Database(entities = [Alarm::class], version = 1, exportSchema = false)
abstract class AlarmDatabase : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao

    companion object {
        @Volatile
        private var INSTANCE: AlarmDatabase? = null
        private const val NUMBER_OF_THREADS = 4
        val writeExecutor: ExecutorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS)
        fun getInstance(context: Context): AlarmDatabase? {
            if (INSTANCE == null) {
                synchronized(AlarmDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = databaseBuilder(context.applicationContext, AlarmDatabase::class.java, "alarm_database").build()
                    }
                }
            }
            return INSTANCE
        }
    }
}