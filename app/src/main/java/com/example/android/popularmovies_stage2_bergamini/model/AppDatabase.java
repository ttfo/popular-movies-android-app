package com.example.android.popularmovies_stage2_bergamini.model;

import android.content.Context;
import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

// most code below from T09b.02
@Database(entities = {Film.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String LOG_TAG = AppDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "popular_movies";
    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME)
                        .addMigrations(MIGRATION_1_2) // REF. https://medium.com/androiddevelopers/understanding-migrations-with-room-f01e04b07929
                        .build();
            }
        }
        Log.d(LOG_TAG, "Getting the database instance");
        return sInstance;
    }

    public abstract FilmDao filmDao();

    // REF. https://medium.com/androiddevelopers/understanding-migrations-with-room-f01e04b07929
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE fav_movies ADD COLUMN original_title VARCHAR(65535)");
            database.execSQL("ALTER TABLE fav_movies ADD COLUMN poster_path VARCHAR(65535)");
            database.execSQL("ALTER TABLE fav_movies ADD COLUMN overview VARCHAR(65535)");
            database.execSQL("ALTER TABLE fav_movies ADD COLUMN popularity INTEGER");
            database.execSQL("ALTER TABLE fav_movies ADD COLUMN vote_avg INTEGER");
            database.execSQL("ALTER TABLE fav_movies ADD COLUMN release_date VARCHAR(65535)");
            database.execSQL("ALTER TABLE fav_movies ADD COLUMN poster_url VARCHAR(65535)");
            database.execSQL("ALTER TABLE fav_movies ADD COLUMN review_url VARCHAR(65535)");
            database.execSQL("ALTER TABLE fav_movies ADD COLUMN trailer_url VARCHAR(65535)");
        }
    };

}
