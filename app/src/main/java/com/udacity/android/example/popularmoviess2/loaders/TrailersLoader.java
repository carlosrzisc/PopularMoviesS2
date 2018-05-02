package com.udacity.android.example.popularmoviess2.loaders;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;

import com.udacity.android.example.popularmoviess2.BuildConfig;
import com.udacity.android.example.popularmoviess2.model.Movie;
import com.udacity.android.example.popularmoviess2.model.Trailer;

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
 * AsyncTaskLoader to fetch trailers
 * Created by carlos on 4/22/18.
 */

public class TrailersLoader extends AsyncTaskLoader<List<Trailer>> {
    private static final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/";
    private static final String API_PARAM = "api_key";
    private static final String API_KEY = BuildConfig.API_KEY;
    private static final String JSON_ROOT = "results";
    private static final String VIDEOS_PATH = "videos";
    private static final String JSON_VIDEO_KEY = "key";
    private static final String YOUTUBE_IMAGE_URL_PREFIX = "http://img.youtube.com/vi/";
    private static final String YOUTUBE_IMAGE_URL_SUFFIX = "/0.jpg";
    private static final String YOUTUBE_LINK_URL = "https://www.youtube.com/watch?v=";

    private String movieId;

    public TrailersLoader(Context context, String movieId) {
        super(context);
        this.movieId = movieId;
    }

    @Override
    public List<Trailer> loadInBackground() {
        Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(movieId)
                .appendPath(VIDEOS_PATH)
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

    private List<Trailer> parseJson(String response) {
        List<Trailer> movieTrailersList = new ArrayList<>();

        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(response);
            JSONArray movies = jsonObject.getJSONArray(JSON_ROOT);
            for (int i=0; i < movies.length(); i++) {
                JSONObject trailerJSON = movies.getJSONObject(i);
                String key = trailerJSON.getString(JSON_VIDEO_KEY);

                Trailer trailer = new Trailer(
                        YOUTUBE_IMAGE_URL_PREFIX + key + YOUTUBE_IMAGE_URL_SUFFIX,
                        YOUTUBE_LINK_URL + key);

                movieTrailersList.add(trailer);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movieTrailersList;
    }
}
