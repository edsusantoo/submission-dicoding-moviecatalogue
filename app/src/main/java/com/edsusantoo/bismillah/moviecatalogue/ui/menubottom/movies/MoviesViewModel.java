package com.edsusantoo.bismillah.moviecatalogue.ui.menubottom.movies;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.edsusantoo.bismillah.moviecatalogue.BuildConfig;
import com.edsusantoo.bismillah.moviecatalogue.data.MoviesRepository;
import com.edsusantoo.bismillah.moviecatalogue.data.network.model.movie.MovieResponse;
import com.edsusantoo.bismillah.moviecatalogue.data.network.observer.ApiObserver;
import com.edsusantoo.bismillah.moviecatalogue.data.network.observer.ApiSingleObserver;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MoviesViewModel extends AndroidViewModel {
    private MoviesRepository repository;
    private MutableLiveData<MovieResponse> dataMovies = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<String> messageError = new MutableLiveData<>();
    private MutableLiveData<String> messageSuccess = new MutableLiveData<>();

    public MoviesViewModel(Application application) {
        super(application);
        repository = new MoviesRepository(application.getApplicationContext());
    }


    LiveData<MovieResponse> getDataMovies() {
        return dataMovies;
    }

    LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    LiveData<String> getMessageError() {
        return messageError;
    }

    LiveData<String> getMessageSuccess() {
        return messageSuccess;
    }

    void getMovies(String language) {
        if (dataMovies.getValue() == null) {
            isLoading.setValue(true);
            repository.getMovie(BuildConfig.API_KEY, language)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ApiSingleObserver<MovieResponse>(repository.getCompositeDisposable()) {
                        @Override
                        public void onSuccessful(MovieResponse response) {
                            isLoading.setValue(false);
                            dataMovies.setValue(response);
                        }

                        @Override
                        public void onFailure(String message) {
                            isLoading.setValue(false);
                            dataMovies.setValue(null);
                        }
                    });
        }
    }

    void refreshMovies(String language) {
        isLoading.setValue(true);
        repository.getMovie(BuildConfig.API_KEY, language)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiSingleObserver<MovieResponse>(repository.getCompositeDisposable()) {
                    @Override
                    public void onSuccessful(MovieResponse response) {
                        isLoading.setValue(false);
                        dataMovies.setValue(response);
                    }

                    @Override
                    public void onFailure(String message) {
                        isLoading.setValue(false);
                        dataMovies.setValue(null);
                    }
                });
    }

    void getSearchMovies(String query) {
        isLoading.setValue(true);
        repository.getSearchMovie(BuildConfig.API_KEY, repository.getLanguage(), query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<MovieResponse>(repository.getCompositeDisposable()) {
                    @Override
                    public void onSuccess(MovieResponse response) {
                        isLoading.setValue(false);
                        dataMovies.setValue(response);
                    }

                    @Override
                    public void onFailure(String message) {
                        isLoading.setValue(false);
                        dataMovies.setValue(null);
                    }
                });
    }

    String getLanguage() {
        return repository.getLanguage();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.onDestroy();
    }
}
