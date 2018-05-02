package com.udacity.android.example.popularmoviess2.loaders;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;

import com.udacity.android.example.popularmoviess2.BuildConfig;
import com.udacity.android.example.popularmoviess2.MoviesFragment;
import com.udacity.android.example.popularmoviess2.data.MoviesContract;
import com.udacity.android.example.popularmoviess2.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * AsyncTaskLoader to fetch movies
 * Created by carlos.
 */

public class MoviesLoader extends AsyncTaskLoader<List<Movie>> {
    private static final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/";
    private static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/w185";
    private static final String BACKDROP_BASE_URL = "https://image.tmdb.org/t/p/w300";
    private static final String API_PARAM = "api_key";
    private static final String API_KEY = BuildConfig.API_KEY;

    private static final String JSON_ROOT = "results";
    private static final String JSON_MOVIE_ID = "id";
    private static final String JSON_MOVIE_TITLE = "title";
    private static final String JSON_MOVIE_POSTER = "poster_path";
    private static final String JSON_MOVIE_RELEASE_DATE = "release_date";
    private static final String JSON_MOVIE_VOTE_AVERAGE = "vote_average";
    private static final String JSON_MOVIE_OVERVIEW = "overview";
    private static final String JSON_MOVIE_BACKDROP = "backdrop_path";

    private String sortBy;
    private Context context;

    public MoviesLoader(Context context, String sortBy) {
        super(context);
        this.sortBy = sortBy;
        this.context = context;
    }

    @Override
    public List<Movie> loadInBackground() {
        if (sortBy.equalsIgnoreCase(MoviesFragment.FAVORITES)) {
            return getFavorites();
        } else {
            Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendPath(sortBy)
                    .appendQueryParameter(API_PARAM, API_KEY)
                    .build();

            String response = null;
            try {
                response = callAPI(new URL(builtUri.toString()));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return parseJson(response);
        }
    }

    private String callAPI(URL url) {
        String result = null;
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream movieInputStream = urlConnection.getInputStream();

            Scanner scanner = new Scanner(movieInputStream);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                result = scanner.next();
            }
        } catch (IOException e) {
                e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return result;
    }

    private List<Movie> parseJson(String response) {
        List<Movie> moviesArrayList = new ArrayList<>();

        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(response);
            JSONArray movies = jsonObject.getJSONArray(JSON_ROOT);
            for (int i=0; i < movies.length(); i++) {
                JSONObject movieJSON = movies.getJSONObject(i);
                String movieId = movieJSON.getString(JSON_MOVIE_ID);
                String posterPath = movieJSON.getString(JSON_MOVIE_POSTER);
                String movieTitle = movieJSON.getString(JSON_MOVIE_TITLE);
                String releaseDate = movieJSON.getString(JSON_MOVIE_RELEASE_DATE);
                String voteAverage = movieJSON.getString(JSON_MOVIE_VOTE_AVERAGE);
                String overview = movieJSON.getString(JSON_MOVIE_OVERVIEW);
                String backdropPath = movieJSON.getString(JSON_MOVIE_BACKDROP);

                Movie movie = new Movie(
                        movieId,
                        movieTitle,
                        POSTER_BASE_URL + posterPath,
                        releaseDate,
                        voteAverage,
                        overview,
                        BACKDROP_BASE_URL + backdropPath);

                moviesArrayList.add(movie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return moviesArrayList;
    }

    private List<Movie> getFavorites() {
        List<Movie> favoriteMovies = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(MoviesContract.MovieEntry.CONTENT_URI, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_TITLE));
                String movie_id = cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_MOVIE_ID));
                String poster = cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_POSTER));
                String backdrop = cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_BACKDROP));
                String overview = cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_OVERVIEW));
                String vote_average = cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE));
                String release_date = cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE));

                Movie movie = new Movie(
                        movie_id,
                        title,
                        poster,
                        release_date,
                        vote_average,
                        overview,
                        backdrop);
                favoriteMovies.add(movie);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return favoriteMovies;
    }
}
