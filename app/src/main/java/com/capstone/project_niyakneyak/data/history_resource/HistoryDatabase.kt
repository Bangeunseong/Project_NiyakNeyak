package com.capstone.project_niyakneyak.data.history_resource

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import com.capstone.project_niyakneyak.data.history_model.HistoryData
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Database(entities = [HistoryData::class], version = 1, exportSchema = false)
abstract class HistoryDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao?

    companion object {
        @Volatile
        private var INSTANCE: HistoryDatabase? = null
        private const val NUMBER_OF_THREADS = 4
        val writeExecutor: ExecutorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS)
        fun getInstance(context: Context): HistoryDatabase? {
            if (INSTANCE == null) {
                synchronized(HistoryDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = databaseBuilder(
                            context.applicationContext,
                            HistoryDatabase::class.java,
                            "history_database"
                        ).build()
                    }
                }
            }
            return INSTANCE
        }
    }
}