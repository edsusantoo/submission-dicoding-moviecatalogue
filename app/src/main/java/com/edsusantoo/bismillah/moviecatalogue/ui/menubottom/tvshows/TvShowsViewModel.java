package com.edsusantoo.bismillah.moviecatalogue.ui.menubottom.tvshows;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.edsusantoo.bismillah.moviecatalogue.BuildConfig;
import com.edsusantoo.bismillah.moviecatalogue.data.MoviesRepository;
import com.edsusantoo.bismillah.moviecatalogue.data.network.model.tvshow.TvShowResponse;
import com.edsusantoo.bismillah.moviecatalogue.data.network.observer.ApiObserver;
import com.edsusantoo.bismillah.moviecatalogue.data.network.observer.ApiSingleObserver;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class TvShowsViewModel extends AndroidViewModel {
    private MoviesRepository repository;
    private MutableLiveData<TvShowResponse> dataTvShows = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public TvShowsViewModel(Application application) {
        super(application);
        repository = new MoviesRepository(application.getApplicationContext());
    }


    LiveData<TvShowResponse> getDataTvShows() {
        return dataTvShows;
    }

    LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    void getTvShow(String language) {
        if (dataTvShows.getValue() == null) {
            isLoading.setValue(true);
            repository.getTvShow(BuildConfig.API_KEY, language)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ApiSingleObserver<TvShowResponse>(repository.getCompositeDisposable()) {
                        @Override
                        public void onSuccessful(TvShowResponse response) {
                            isLoading.setValue(false);
                            dataTvShows.setValue(response);
                        }

                        @Override
                        public void onFailure(String message) {
                            isLoading.setValue(false);
                            dataTvShows.setValue(null);
                        }
                    });
        }
    }

    void refreshMovies(String language) {
        isLoading.setValue(true);
        repository.getTvShow(BuildConfig.API_KEY, language)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiSingleObserver<TvShowResponse>(repository.getCompositeDisposable()) {
                    @Override
                    public void onSuccessful(TvShowResponse response) {
                        isLoading.setValue(false);
                        dataTvShows.setValue(response);
                    }

                    @Override
                    public void onFailure(String message) {
                        isLoading.setValue(false);
                        dataTvShows.setValue(null);
                    }
                });
    }

    void getSearchTvMovies(String query) {
        isLoading.setValue(true);
        repository.getSearchTvShow(BuildConfig.API_KEY, repository.getLanguage(), query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<TvShowResponse>(repository.getCompositeDisposable()) {
                    @Override
                    public void onSuccess(TvShowResponse response) {
                        isLoading.setValue(false);
                        dataTvShows.setValue(response);
                    }

                    @Override
                    public void onFailure(String message) {
                        isLoading.setValue(false);
                        dataTvShows.setValue(null);
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
