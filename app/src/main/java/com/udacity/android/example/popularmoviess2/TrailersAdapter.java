package com.udacity.android.example.popularmoviess2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.udacity.android.example.popularmoviess2.model.Movie;
import com.udacity.android.example.popularmoviess2.model.Trailer;

import java.util.ArrayList;
import java.util.List;

/**
 * Movies Recycler view adapter
 * Created by carlos.
 */

class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailerHolder> {

    private Context context;
    private OnTrailerClickListener listener;
    private List<Trailer> trailers;

    TrailersAdapter(Context context, OnTrailerClickListener listener) {
        this.context = context;
        this.listener = listener;
        trailers = new ArrayList<>();
    }

    @NonNull
    @Override
    public TrailerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TrailerHolder(LayoutInflater.from(context).inflate(R.layout.item_trailer, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerHolder holder, int position) {
        holder.bind(trailers.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return trailers.size();
    }

    void setTrailers(List<Trailer> movies) {
        this.trailers = movies;
    }

    void clear() {
        this.trailers.clear();
    }

    public ArrayList<Trailer> getTrailers() {
        return (ArrayList<Trailer>) trailers;
    }

    class TrailerHolder extends RecyclerView.ViewHolder {

        TrailerHolder(View itemView) {
            super(itemView);
        }

        private void bind(final Trailer trailer, final OnTrailerClickListener listener) {
            ImageView trailerView = itemView.findViewById(R.id.image_trailer);
            Picasso.get().load(trailer.getImage()).into(trailerView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClick(trailers.get(getAdapterPosition()));
                }
            });
        }
    }

    interface OnTrailerClickListener {
        void onClick(Trailer trailer);
    }
}
