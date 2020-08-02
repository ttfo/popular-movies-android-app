package com.example.android.popularmovies_stage2_bergamini.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FilmDao {

    @Query("SELECT * FROM fav_movies ORDER BY id")
    LiveData<List<Film>> loadAllFilms();

    @Insert
    void insertFilm(Film film);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateFilm(Film film);

    @Delete
    void deleteFilm(Film film);

    // REF. https://www.youtube.com/watch?time_continue=38&v=K3Ul4pQ7tYw&feature=emb_logo
    @Query("SELECT * FROM fav_movies WHERE id = :id") // colon is used to refer to the param provided
    LiveData<Film> loadFilmById(int id);

    @Query("SELECT EXISTS(SELECT * FROM fav_movies WHERE id = :id)")
    Boolean isMovieInFavTable(int id);

}
