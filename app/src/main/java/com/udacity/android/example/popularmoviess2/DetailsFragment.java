package com.udacity.android.example.popularmoviess2;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.android.example.popularmoviess2.data.MoviesContract;
import com.udacity.android.example.popularmoviess2.loaders.ReviewsLoader;
import com.udacity.android.example.popularmoviess2.loaders.TrailersLoader;
import com.udacity.android.example.popularmoviess2.model.Movie;
import com.squareup.picasso.Picasso;
import com.udacity.android.example.popularmoviess2.model.Review;
import com.udacity.android.example.popularmoviess2.model.Trailer;
import com.udacity.android.example.popularmoviess2.utilities.Utility;

import java.util.List;


/**
 * Movie Details fragment
 */
public class DetailsFragment extends Fragment {

    public static final String ARG_MOVIE = "param_movie";
    private Movie paramMovie;
    TrailersAdapter trailersAdapter;
    ReviewsAdapter reviewsAdapter;
    ImageView favoriteSwitch;

    public DetailsFragment() { }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param movie Movie.
     * @return A new instance of fragment DetailsFragment.
     */
    public static DetailsFragment newInstance(Movie movie) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_MOVIE, movie);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            paramMovie = getArguments().getParcelable(ARG_MOVIE);

            trailersAdapter = new TrailersAdapter(getActivity(), new TrailersAdapter.OnTrailerClickListener() {
                @Override
                public void onClick(Trailer trailer) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailer.getLink()));
                    if (getActivity() != null) {
                        getActivity().startActivity(intent);
                    }
                }
            });
            reviewsAdapter = new ReviewsAdapter(getActivity());

            LoaderManager.LoaderCallbacks trailersCallbacks = new LoaderManager.LoaderCallbacks<List<Trailer>>() {

                @NonNull @Override
                public Loader<List<Trailer>> onCreateLoader(int id, Bundle args) {
                    return new TrailersLoader(getActivity(), paramMovie.getId());
                }

                @Override
                public void onLoadFinished(@NonNull Loader<List<Trailer>> loader, List<Trailer> data) {
                    trailersAdapter.setTrailers(data);
                    trailersAdapter.notifyDataSetChanged();
                }

                @Override
                public void onLoaderReset(@NonNull Loader<List<Trailer>> loader) {
                    trailersAdapter.clear();
                }
            };
            LoaderManager.LoaderCallbacks reviewsCallbacks = new LoaderManager.LoaderCallbacks<List<Review>>() {

                @NonNull
                @Override
                public Loader<List<Review>> onCreateLoader(int id, @Nullable Bundle args) {
                    return new ReviewsLoader(getActivity(), paramMovie.getId());
                }

                @Override
                public void onLoadFinished(@NonNull Loader<List<Review>> loader, List<Review> data) {
                    reviewsAdapter.setReviews(data);
                    reviewsAdapter.notifyDataSetChanged();
                }

                @Override
                public void onLoaderReset(@NonNull Loader<List<Review>> loader) {
                    reviewsAdapter.clear();
                }
            };
            if (savedInstanceState!= null && savedInstanceState.containsKey("TRAILERS_KEY")) {
                List<Trailer> trailers = savedInstanceState.getParcelableArrayList("TRAILERS_KEY");
                List<Review> reviews = savedInstanceState.getParcelableArrayList("REVIEWS_KEY");
                trailersAdapter.setTrailers(trailers);
                reviewsAdapter.setReviews(reviews);
            } else {
                if (getActivity() != null && Utility.hasInternetConnection(getActivity())) {
                    getActivity().getSupportLoaderManager().initLoader(0, null, trailersCallbacks).forceLoad();
                    getActivity().getSupportLoaderManager().initLoader(1, null, reviewsCallbacks).forceLoad();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.error_no_connection), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);

        ImageView moviePoster = rootView.findViewById(R.id.details_image_poster);
        TextView movieTitle = rootView.findViewById(R.id.details_movie_title);
        TextView movieRelease = rootView.findViewById(R.id.details_movie_release);
        TextView movieRating = rootView.findViewById(R.id.details_movie_rating);
        TextView movieOverview = rootView.findViewById(R.id.details_movie_overview);
        RecyclerView trailersRv = rootView.findViewById(R.id.list_trailers);
        RecyclerView reviewsRv = rootView.findViewById(R.id.list_reviews);
        favoriteSwitch = rootView.findViewById(R.id.details_favorite_switch);

        Picasso.get().load(paramMovie.getPosterPath()).into(moviePoster);
        moviePoster.setContentDescription(paramMovie.getTitle());
        movieTitle.setText(paramMovie.getTitle());
        movieRelease.setText(String.format(getString(R.string.text_release_date), paramMovie.getReleaseDate()));
        movieRating.setText(String.format(getString(R.string.text_rating), paramMovie.getVoteAverage()));
        movieOverview.setText(paramMovie.getOverview());

        trailersRv.setHasFixedSize(true);
        trailersRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        trailersRv.setAdapter(trailersAdapter);

        reviewsRv.setHasFixedSize(true);
        reviewsRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        reviewsRv.setAdapter(reviewsAdapter);


        if (isFavoriteMovie(paramMovie.getId()) && getActivity() != null) {
            favoriteSwitch.setImageDrawable(
                ContextCompat.getDrawable(getActivity(), R.drawable.ic_favorite_black_24dp));
        }
        favoriteSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFavoriteMovie(paramMovie.getId())) {
                    removeMovie(paramMovie.getId());
                } else {
                    addMovie(paramMovie);
                }
            }
        });
        return rootView;
    }

    private void addMovie(Movie movie) {
        if (getActivity() != null && getActivity().getContentResolver() != null) {
            ContentValues values = new ContentValues();
            values.put(MoviesContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
            values.put(MoviesContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
            values.put(MoviesContract.MovieEntry.COLUMN_POSTER, movie.getPosterPath());
            values.put(MoviesContract.MovieEntry.COLUMN_BACKDROP, movie.getBackDropPath());
            values.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
            values.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
            values.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());

            getActivity().getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, values);

            favoriteSwitch.setImageDrawable(
                    ContextCompat.getDrawable(getActivity(), R.drawable.ic_favorite_black_24dp));
        }
    }

    private void removeMovie(String id) {
        if (getActivity() != null && getActivity().getContentResolver() != null) {
            getActivity().getContentResolver().delete(
                    MoviesContract.MovieEntry.CONTENT_URI,
                    MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                    new String[]{id});
            favoriteSwitch.setImageDrawable(
                    ContextCompat.getDrawable(getActivity(), R.drawable.ic_favorite_border_black_24dp));
        }
    }

    private boolean isFavoriteMovie(String id) {
        boolean movieExists = false;

        if (getActivity() != null && getActivity().getContentResolver() != null) {
            Cursor cursor = getActivity().getContentResolver().query(
                    MoviesContract.MovieEntry.CONTENT_URI,
                    null,
                    MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                    new String[] {id},
                    null);
            if (cursor != null) {
                if (cursor.getCount() > 0)
                    movieExists = true;
                cursor.close();
            }
        }
        return movieExists;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putParcelableArrayList("TRAILERS_KEY", trailersAdapter.getTrailers());
        savedInstanceState.putParcelableArrayList("REVIEWS_KEY", reviewsAdapter.getReviews());
    }
}
