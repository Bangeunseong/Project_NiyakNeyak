package com.capstone.project_niyakneyak.data.history_resource;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.capstone.project_niyakneyak.data.history_model.HistoryData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {HistoryData.class}, version = 1, exportSchema = false)
public abstract class HistoryDatabase extends RoomDatabase {
    public abstract HistoryDao historyDao();
    private static volatile HistoryDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService writeExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static HistoryDatabase getInstance(final Context context){
        if(INSTANCE == null){
            synchronized (HistoryDatabase.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), HistoryDatabase.class, "history_database").build();
                }
            }
        }
        return INSTANCE;
    }
}
