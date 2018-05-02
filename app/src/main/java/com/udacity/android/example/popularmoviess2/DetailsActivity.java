package com.udacity.android.example.popularmoviess2;

import android.graphics.Color;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.udacity.android.example.popularmoviess2.model.Movie;
import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Movie movie = getIntent().getParcelableExtra(DetailsFragment.ARG_MOVIE);

        collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(this, R.color.transparent));
        collapsingToolbar.setCollapsedTitleTextColor(Color.rgb(255, 255, 255));
        collapsingToolbar.setTitle(movie.getTitle());

        ImageView backdropImageView = findViewById(R.id.expandedImage);
        Picasso.get().load(movie.getBackDropPath()).into(backdropImageView);
        backdropImageView.setContentDescription(movie.getTitle());

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.details_container, DetailsFragment.newInstance(movie)).commit();
        }
    }
}
