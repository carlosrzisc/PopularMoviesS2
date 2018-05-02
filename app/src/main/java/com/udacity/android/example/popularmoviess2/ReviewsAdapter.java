package com.udacity.android.example.popularmoviess2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.android.example.popularmoviess2.model.Review;
import com.udacity.android.example.popularmoviess2.model.Trailer;

import java.util.ArrayList;
import java.util.List;

/**
 * Movies Recycler view adapter
 * Created by carlos.
 */

class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewHolder> {

    private Context context;
    private List<Review> reviews;

    ReviewsAdapter(Context context) {
        this.context = context;
        reviews = new ArrayList<>();
    }

    @NonNull
    @Override
    public ReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReviewHolder(LayoutInflater.from(context).inflate(R.layout.item_review, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewHolder holder, int position) {
        holder.bind(reviews.get(position));
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    void clear() {
        this.reviews.clear();
    }

    public ArrayList<Review> getReviews() {
        return (ArrayList<Review>) reviews;
    }

    class ReviewHolder extends RecyclerView.ViewHolder {

        ReviewHolder(View itemView) {
            super(itemView);
        }

        private void bind(final Review review) {
            TextView author = itemView.findViewById(R.id.review_author);
            author.setText(review.getAuthor());
            TextView content = itemView.findViewById(R.id.review_content);
            content.setText(review.getContent());
        }
    }
}
