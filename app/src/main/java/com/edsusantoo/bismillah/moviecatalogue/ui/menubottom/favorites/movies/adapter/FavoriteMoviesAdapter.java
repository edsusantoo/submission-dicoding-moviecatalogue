package com.edsusantoo.bismillah.moviecatalogue.ui.menubottom.favorites.movies.adapter;

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
import com.edsusantoo.bismillah.moviecatalogue.data.db.model.Movie;
import com.edsusantoo.bismillah.moviecatalogue.ui.detailmovie.DetailMovieActivity;
import com.edsusantoo.bismillah.moviecatalogue.utils.Constant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoriteMoviesAdapter extends RecyclerView.Adapter<FavoriteMoviesAdapter.FavoriteMoviesViewHolder> {

    private ArrayList<Movie> dataMovie = new ArrayList<>();
    private Context context;


    public FavoriteMoviesAdapter(Context context) {
        this.context = context;
    }

    public void addMovie(List<Movie> movies) {
        dataMovie.addAll(movies);
    }


    public void refresh() {
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FavoriteMoviesAdapter.FavoriteMoviesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite, viewGroup, false);
        return new FavoriteMoviesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteMoviesAdapter.FavoriteMoviesViewHolder holder, int position) {
        final Movie movie = dataMovie.get(position);
        holder.tvTitle.setText(movie.getMovieName());
        holder.tvDescription.setText(movie.getDescription());
        Glide.with(context).load(movie.getMoviePhoto())
                .centerCrop()
                .placeholder(R.drawable.ic_image_grey_100dp)
                .error(R.drawable.ic_broken_image_grey_100dp)
                .into(holder.imgMovie);

        holder.cvFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, DetailMovieActivity.class);
                com.edsusantoo.bismillah.moviecatalogue.data.Movie dataMovie = new com.edsusantoo.bismillah.moviecatalogue.data.Movie();
                dataMovie.setMovieId(movie.getMovieId());
                dataMovie.setTitle(movie.getMovieName());
                dataMovie.setDescription(movie.getDescription());
                dataMovie.setPhoto(movie.getMoviePhoto());
                dataMovie.setType(Constant.TYPE_MOVIE);
                i.putExtra(DetailMovieActivity.EXTRA_MOVIE_DETAIL, dataMovie);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (dataMovie == null) return 0;
        return dataMovie.size();
    }

    class FavoriteMoviesViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_description)
        TextView tvDescription;
        @BindView(R.id.img_movie)
        ImageView imgMovie;
        @BindView(R.id.cv_item_favorite)
        CardView cvFavorite;

        FavoriteMoviesViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
