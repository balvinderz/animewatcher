package com.stuffbox.webscraper.database;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.stuffbox.webscraper.models.Anime;

@Database(entities = Anime.class,version =1,exportSchema = false)
public abstract class AnimeDatabase extends RoomDatabase {
    private static final String LOG_TAG = AnimeDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "anime_db";
    private static AnimeDatabase sInstance;

    public static AnimeDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AnimeDatabase.class, DATABASE_NAME)
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build();
            }
        }
        Log.d(LOG_TAG, "Getting the database instance");
        return sInstance;
    }

    public abstract AnimeDao animeDao();
}