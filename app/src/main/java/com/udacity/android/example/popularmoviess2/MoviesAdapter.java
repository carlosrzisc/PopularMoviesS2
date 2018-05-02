package com.udacity.android.example.popularmoviess2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.udacity.android.example.popularmoviess2.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Movies Recycler view adapter
 * Created by carlos.
 */

class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieHolder> {

    private Context context;
    private OnMovieClickListener listener;
    private List<Movie> movies;

    MoviesAdapter(Context context, OnMovieClickListener listener) {
        this.context = context;
        this.listener = listener;
        movies = new ArrayList<>();
    }

    @NonNull
    @Override
    public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MovieHolder(LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MovieHolder holder, int position) {
        holder.bind(movies.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    void clear() {
        this.movies.clear();
    }

    class MovieHolder extends RecyclerView.ViewHolder {

        MovieHolder(View itemView) {
            super(itemView);
        }

        private void bind(final Movie movie, final OnMovieClickListener listener) {
            ImageView moviePoster = itemView.findViewById(R.id.item_poster);
            Picasso.get().load(movie.getPosterPath()).into(moviePoster);
            moviePoster.setContentDescription(movie.getTitle());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClick(movies.get(getAdapterPosition()));
                }
            });
        }
    }

    interface OnMovieClickListener {
        void onClick(Movie movie);
    }
}
