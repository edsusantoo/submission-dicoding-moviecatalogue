package com.edsusantoo.bismillah.moviecatalogue.ui.menubottom.favorites.movies;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edsusantoo.bismillah.moviecatalogue.R;
import com.edsusantoo.bismillah.moviecatalogue.data.db.model.Favorites;
import com.edsusantoo.bismillah.moviecatalogue.data.db.model.Movie;
import com.edsusantoo.bismillah.moviecatalogue.ui.menubottom.favorites.movies.adapter.FavoriteMoviesAdapter;
import com.edsusantoo.bismillah.moviecatalogue.utils.Constant;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoriteMoviesFragment extends Fragment {

    @BindView(R.id.rv_favorite_movies)
    RecyclerView rvFavoriteMovies;
    private FavoriteMoviesAdapter adapter;

    private FavoriteMoviesViewModel favoriteMoviesViewModel;
    private Observer<List<Favorites>> getFavorite = new Observer<List<Favorites>>() {
        @Override
        public void onChanged(@Nullable List<Favorites> favorites) {
            if (favorites != null) {
                for (Favorites data : favorites) {
                    favoriteMoviesViewModel.getMovie(data.getMovieId(), Constant.TYPE_MOVIE);
                }
            }
        }
    };

    private Observer<List<Movie>> getMovie = new Observer<List<Movie>>() {
        @Override
        public void onChanged(@Nullable List<Movie> movies) {
            if (movies != null && movies.size() != 0) {
                adapter.addMovie(movies);
                adapter.refresh();
            }
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite_movies, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        favoriteMoviesViewModel = ViewModelProviders.of(this).get(FavoriteMoviesViewModel.class);
        favoriteMoviesViewModel.getFavorite().observe(this, getFavorite);
        favoriteMoviesViewModel.getMovie().observe(this, getMovie);

        favoriteMoviesViewModel.getMovieFavorite();

        setupRecyclerView();

    }

    private void setupRecyclerView() {
        adapter = new FavoriteMoviesAdapter(getActivity());
        rvFavoriteMovies.setLayoutManager(new LinearLayoutManager(getContext()));
        rvFavoriteMovies.setAdapter(adapter);
        rvFavoriteMovies.setHasFixedSize(true);
    }
}
