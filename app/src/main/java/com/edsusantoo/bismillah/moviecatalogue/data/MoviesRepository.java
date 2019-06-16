package com.edsusantoo.bismillah.moviecatalogue.data;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import com.edsusantoo.bismillah.moviecatalogue.data.db.RootMoviesDB;
import com.edsusantoo.bismillah.moviecatalogue.data.db.dao.FavoriteDao;
import com.edsusantoo.bismillah.moviecatalogue.data.db.dao.MovieDao;
import com.edsusantoo.bismillah.moviecatalogue.data.db.dao.UserDao;
import com.edsusantoo.bismillah.moviecatalogue.data.db.model.Favorites;
import com.edsusantoo.bismillah.moviecatalogue.data.db.model.Movie;
import com.edsusantoo.bismillah.moviecatalogue.data.db.model.User;
import com.edsusantoo.bismillah.moviecatalogue.data.network.ApiServices;
import com.edsusantoo.bismillah.moviecatalogue.data.network.RetrofitConfig;
import com.edsusantoo.bismillah.moviecatalogue.data.network.model.movie.MovieResponse;
import com.edsusantoo.bismillah.moviecatalogue.data.network.model.tvshow.TvShowResponse;
import com.edsusantoo.bismillah.moviecatalogue.data.pref.SharedPref;
import com.edsusantoo.bismillah.moviecatalogue.utils.Constant;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;

public class MoviesRepository implements Repository {
    private ApiServices apiServices;
    private CompositeDisposable compositeDisposable;
    private SharedPref pref;
    private MovieDao movieDao;
    private UserDao userDao;
    private FavoriteDao favoriteDao;

    private LiveData<List<Movie>> allMovies;
    private LiveData<List<Favorites>> allFavorites;
    private LiveData<List<User>> allUsers;

    public MoviesRepository(Context context) {
        apiServices = RetrofitConfig.getApiServices();
        compositeDisposable = new CompositeDisposable();
        pref = new SharedPref(context);
        RootMoviesDB rootMoviesDB = RootMoviesDB.getDatabase(context);

        //RoomDao
        movieDao = rootMoviesDB.movieDao();
        userDao = rootMoviesDB.userDao();
        favoriteDao = rootMoviesDB.favoriteDao();

        allMovies = movieDao.getAll();


    }


    @Override
    public Observable<MovieResponse> getMovie(String api_key, String language) {
        return apiServices.getMovie(api_key, language);
    }

    @Override
    public Observable<TvShowResponse> getTvShow(String api_key, String language) {
        return apiServices.getTvMovie(api_key, language);
    }

    @Override
    public void insertUser(User user) {
        userDao.insert(user);
    }

    @Override
    public void insertMovie(Movie movie) {
        movieDao.insert(movie);
    }

    @Override
    public void insertFavorite(Favorites favorites) {
        favoriteDao.insert(favorites);
    }

    @Override
    public LiveData<List<Movie>> getAllMovie() {
        return allMovies;
    }

    @Override
    public LiveData<List<User>> getAllUser() {
        return allUsers;
    }

    @Override
    public LiveData<List<Favorites>> getAllFavorites() {
        return allFavorites;
    }

    @Override
    public String getLanguage() {
        return pref.getSharedPref().getString(Constant.PREF_LANGUAGE, "");
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
    }

    public CompositeDisposable getCompositeDisposable() {
        return compositeDisposable;
    }
}
