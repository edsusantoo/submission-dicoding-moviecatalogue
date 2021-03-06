package com.edsusantoo.bismillah.moviecatalogue.ui.menubottom.movies.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.edsusantoo.bismillah.moviecatalogue.R;
import com.edsusantoo.bismillah.moviecatalogue.data.Movie;
import com.edsusantoo.bismillah.moviecatalogue.data.network.model.movie.ResultsItem;
import com.edsusantoo.bismillah.moviecatalogue.ui.detailmovie.DetailMovieActivity;
import com.edsusantoo.bismillah.moviecatalogue.utils.Constant;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListMoviesAdapter extends RecyclerView.Adapter<ListMoviesAdapter.ListMoviesViewHolder> {

    private Context context;
    private List<ResultsItem> movies;

    public ListMoviesAdapter(Context context, List<ResultsItem> movies) {
        this.context = context;
        this.movies = movies;
    }


    @NonNull
    @Override
    public ListMoviesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_movie, viewGroup, false);
        return new ListMoviesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListMoviesViewHolder holder, int position) {
        final ResultsItem movie = movies.get(position);
        final String image_url = "https://image.tmdb.org/t/p/w500" + movie.getBackdropPath();
        final double rate = movie.getVoteAverage() * 10;
        String description = movie.getOverview();
        if (description != null && description.isEmpty()) {
            description = context.getResources().getString(R.string.no_translations);
        }

        holder.tvTittle.setText(movie.getTitle());
        holder.tvDateRelease.setText(movie.getReleaseDate());
        holder.tvDescription.setText(description);
        Glide.with(context).load(image_url)
                .centerCrop()
                .placeholder(R.drawable.ic_image_grey_100dp)
                .error(R.drawable.ic_broken_image_grey_100dp)
                .into(holder.imgMovie);
        final String finalDescription = description;
        holder.cvMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, DetailMovieActivity.class);
                Movie dataMovie = new Movie();
                dataMovie.setMovieId(movie.getId());
                dataMovie.setTitle(movie.getTitle());
                dataMovie.setDescription(finalDescription);
                dataMovie.setPhoto(image_url);
                dataMovie.setType(Constant.TYPE_MOVIE);
                i.putExtra(DetailMovieActivity.EXTRA_MOVIE_DETAIL, dataMovie);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }


    class ListMoviesViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_title)
        TextView tvTittle;
        @BindView(R.id.tv_description)
        TextView tvDescription;
        @BindView(R.id.tv_date_release)
        TextView tvDateRelease;
        @BindView(R.id.img_movie)
        ImageView imgMovie;
        @BindView(R.id.cv_list_movie)
        CardView cvMovie;

        ListMoviesViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
