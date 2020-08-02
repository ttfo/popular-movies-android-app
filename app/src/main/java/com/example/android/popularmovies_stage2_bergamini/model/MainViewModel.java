package com.example.android.popularmovies_stage2_bergamini.model;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    // Constant for logging
    private static final String TAG = MainViewModel.class.getSimpleName();

    // Add a tasks member variable for a list of Film objects wrapped in a LiveData
    private LiveData<List<Film>> films;

    public MainViewModel(@NonNull Application application) {
        super(application);
        // In the constructor use the loadAllTasks of the taskDao to initialize the tasks variable
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving the tasks from the DataBase");
        films = database.filmDao().loadAllFilms();
    }

    // Create a getter to retrieve fav movies
    public LiveData<List<Film>> getFavMovies() {
        return films;
    }
}
