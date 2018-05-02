package com.udacity.android.example.popularmoviess2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.udacity.android.example.popularmoviess2.loaders.MoviesLoader;
import com.udacity.android.example.popularmoviess2.model.Movie;
import com.udacity.android.example.popularmoviess2.utilities.Utility;

import java.util.List;


/**
 * Movies fragment
 */
public class MoviesFragment extends Fragment {
    private final static int COLUMNS = 3;
    private final static String URL_POPULARITY = "popular";
    private final static String URL_RATING = "top_rated";
    public final static String FAVORITES = "favorites";

    private final static String PREF = "sort";

    private LoaderManager.LoaderCallbacks loaderCallbacks;
    private MoviesAdapter moviesAdapter;
    private SharedPreferences preferences;

    public MoviesFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        setHasOptionsMenu(true);
        moviesAdapter = new MoviesAdapter(getActivity(), new MoviesAdapter.OnMovieClickListener() {
            @Override
            public void onClick(Movie movie) {
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra(DetailsFragment.ARG_MOVIE, movie);
                startActivity(intent);
            }
        });

        loaderCallbacks = new LoaderManager.LoaderCallbacks<List<Movie>>() {

            @NonNull
            @Override
            public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
                return new MoviesLoader(getActivity(), getSortBySelection());
            }

            @Override
            public void onLoadFinished(@NonNull Loader<List<Movie>> loader, List<Movie> data) {
                moviesAdapter.setMovies(data);
                moviesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLoaderReset(@NonNull Loader<List<Movie>> loader) {
                moviesAdapter.clear();
            }
        };

        FragmentActivity activity = getActivity();
        if (activity!= null && (Utility.hasInternetConnection(activity) || getSortBySelection().equalsIgnoreCase(FAVORITES))) {
            activity.getSupportLoaderManager().initLoader(0, null, loaderCallbacks).forceLoad();
        } else {
            Toast.makeText(activity, getString(R.string.error_no_connection), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getSortBySelection().equalsIgnoreCase(FAVORITES)) {
            fetchMovies();
        }
    }

    private String getSortBySelection() {
        if (preferences == null) {
            preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        }
        return preferences.getString(PREF, URL_POPULARITY);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movies, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerview_movies);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), COLUMNS));
        recyclerView.setAdapter(moviesAdapter);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_movies_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.apply();
        switch (item.getItemId()) {
            case R.id.popularity: editor.putString(PREF, URL_POPULARITY); break;
            case R.id.rating: editor.putString(PREF, URL_RATING); break;
            case R.id.favorites: editor.putString(PREF, FAVORITES);break;
            default: return false;
        }
        editor.apply();
        fetchMovies();
        item.setChecked(true);
        return super.onOptionsItemSelected(item);
    }

    private void fetchMovies() {
        FragmentActivity activity = getActivity();
        if (activity!= null && (Utility.hasInternetConnection(activity) || getSortBySelection().equalsIgnoreCase(FAVORITES))) {
            activity.getSupportLoaderManager().restartLoader(0, null, loaderCallbacks).forceLoad();
        } else {
            Toast.makeText(activity, getString(R.string.error_no_connection), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        String sortBy = preferences.getString(PREF, URL_POPULARITY);
        switch (sortBy) {
            case FAVORITES: menu.findItem(R.id.favorites).setChecked(true); break;
            case URL_RATING: menu.findItem(R.id.rating).setChecked(true); break;
            case URL_POPULARITY: menu.findItem(R.id.popularity).setChecked(true);
        }
    }
}
