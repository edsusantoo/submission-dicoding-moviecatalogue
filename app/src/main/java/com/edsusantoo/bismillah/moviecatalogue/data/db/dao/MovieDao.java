package com.edsusantoo.bismillah.moviecatalogue.data.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.database.Cursor;

import com.edsusantoo.bismillah.moviecatalogue.data.db.model.Movie;

import java.util.List;

import io.reactivex.Maybe;

@Dao
public interface MovieDao {
    @Query("SELECT * FROM movie")
    LiveData<List<Movie>> getAll();

    @Query("SELECT * FROM movie")
    Cursor getAllMovieContentProvider();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Movie... movies);

    @Delete
    void delete(Movie... movies);

    @Query("SELECT * FROM movie WHERE id =:movieId AND type =:type")
    Maybe<List<Movie>> getMovie(int movieId, String type);

    @Query("SELECT * FROM movie WHERE id =:movieId AND type =:type")
    List<Movie> getMovieWidget(int movieId, String type);

}
