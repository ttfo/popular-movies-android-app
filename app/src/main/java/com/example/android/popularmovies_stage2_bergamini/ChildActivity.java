package com.example.android.popularmovies_stage2_bergamini;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies_stage2_bergamini.model.AppDatabase;
import com.example.android.popularmovies_stage2_bergamini.model.AppExecutors;
import com.example.android.popularmovies_stage2_bergamini.model.Film;
import com.squareup.picasso.Picasso;

import java.io.Serializable;

public class ChildActivity extends AppCompatActivity implements Serializable {

    // Field to store movie details
    private TextView mDisplayText;
    // Field to store movie poster image
    private ImageView mDisplayPoster;
    // Fields to store 'star' buttons in on/off status
    private ImageButton starOn;
    private ImageButton starOff;

    // New Film object to store our movie details
    private Film clickedFilmObj = new Film();

    // Member variable for the Database
    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);

        mDisplayText = (TextView) findViewById(R.id.tv_movie_details);
        mDisplayPoster = (ImageView) findViewById(R.id.poster_child_iv);
        starOn = (ImageButton) findViewById(R.id.star_on_ib);
        starOff = (ImageButton) findViewById(R.id.star_off_ib);

        Intent movieClickedIntent = getIntent();

        // init DB instance
        mDb = AppDatabase.getInstance(getApplicationContext());

        if (movieClickedIntent.hasExtra(getResources().getString(R.string.film_object_label))) {
            populateUI();
        }


        // Toggle star button
        starOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                starOn.setVisibility(View.VISIBLE);
                starOff.setVisibility(View.INVISIBLE);
                starOn.bringToFront();

                //Log.i("STAR", "2");

                clickedFilmObj.setFavourite(true);

                // Adds movies to fav table in ROOM
                // CHECK onSaveButtonClicked method in T09.10
                final Film filmInFavTable = clickedFilmObj;

                // call the diskIO execute method with a new Runnable and implement its run method
                // also ref. https://www.youtube.com/watch?time_continue=203&v=c43ruIIZAMg&feature=emb_logo
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    @NonNull
                    public void run() {
                        // Check if filmId is already in DB before adding it again
                        // Otherwise SQLiteConstraintException: UNIQUE constraint failed: fav_movies.id would show
                        // Adds movie to our DB of favourite movies
                        Boolean filmInDB = mDb.filmDao().isMovieInFavTable(clickedFilmObj.getId());
                        if (!filmInDB) {
                            mDb.filmDao().insertFilm(filmInFavTable);
                        }
                        finish();
                    }
                });

                Toast.makeText(ChildActivity.this, clickedFilmObj.getOriginalTitle() + " added to your fav list!", Toast.LENGTH_SHORT).show();
            }
        });

        starOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                starOff.setVisibility(View.VISIBLE);
                starOn.setVisibility(View.INVISIBLE);
                starOff.bringToFront();

                //Log.i("STAR", "4");

                clickedFilmObj.setFavourite(false);

                // Removes movie from fav table in ROOM
                final Film filmOutFavTable = clickedFilmObj;
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @NonNull
                    @Override
                    public void run() {
                        // Removes movie from our DB of favourite movies
                        Boolean filmInDB = mDb.filmDao().isMovieInFavTable(clickedFilmObj.getId());
                        if (filmInDB) {
                            mDb.filmDao().deleteFilm(filmOutFavTable);
                        }
                        finish();
                    }
                });

                Toast.makeText(ChildActivity.this, clickedFilmObj.getOriginalTitle() + " removed from your fav list!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void populateUI() {
        // REF. https://stackoverflow.com/questions/2736389/how-to-pass-an-object-from-one-activity-to-another-on-android
        clickedFilmObj = (Film) getIntent().getSerializableExtra(getResources().getString(R.string.film_object_label));

        setTitle(clickedFilmObj.getOriginalTitle());

        StringBuilder movieDetailsBuild = new StringBuilder();
        movieDetailsBuild.append("Title: " + clickedFilmObj.getOriginalTitle()).append("\n\n")
                            .append("Release date: " + clickedFilmObj.getReleaseDate()).append("\n\n")
                            .append("Plot synopsis: " + clickedFilmObj.getOverview()).append("\n\n")
                            .append("User Rating: " + clickedFilmObj.getVoteAverage()).append("\n\n")
                            .append("Popularity index: " + clickedFilmObj.getPopularity()).append("\n\n");
        String movieClickedDetails = movieDetailsBuild.toString();

        mDisplayText.setText(movieClickedDetails);

        Picasso.with(this)
                .load(clickedFilmObj.getPosterURLAsString())
                .into(mDisplayPoster);

        // call the diskIO execute method with a new Runnable and implement its run method
        // also ref. https://www.youtube.com/watch?time_continue=203&v=c43ruIIZAMg&feature=emb_logo
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            @NonNull
            public void run() {
                // Otherwise SQLiteConstraintException: UNIQUE constraint failed: fav_movies.id would show
                // Adds movie to our DB of favourite movies
                Boolean filmInDB = mDb.filmDao().isMovieInFavTable(clickedFilmObj.getId());
                if (filmInDB) {
                    clickedFilmObj.setFavourite(true);
                    // If film was flagged as favourite by user, show 'star on' button, or hide if not fav
                    starOn.setVisibility(View.VISIBLE);
                    starOff.setVisibility(View.INVISIBLE);
                    Log.i("FAV_STATUS_CHECK", "Is fav!");
                } else {
                    clickedFilmObj.setFavourite(false);
                    starOff.setVisibility(View.VISIBLE);
                    starOn.setVisibility(View.INVISIBLE);
                    Log.i("FAV_STATUS_CHECK", "Is not fav!");
                }
                //finish(); // finish() prevents ChildActivity to load
            }
        });

    }


    /**
     * This method is called when the Watch Trailer button is clicked. It will open the website
     * specified by the URL represented by the variable urlAsString using implicit Intents.
     *
     * @param v Button that was clicked.
     */
    public void onClickOpenTrailerOnYT(View v) {

        //clickedFilmObj.getTrailerOnYouTubeURL().clear(); // <= just to test Toast
        if (clickedFilmObj.getTrailerOnYouTubeURL().isEmpty()) {
            Toast.makeText(ChildActivity.this, getResources().getString(R.string.no_trailers), Toast.LENGTH_SHORT).show();
        } else {
            String urlAsString = clickedFilmObj.getTrailerOnYouTubeURL();
            openWebPage(urlAsString);
        }

    }


    /**
     * This method is called when the Read Reviews button is clicked. It will open the website
     * specified by the URL represented by the variable urlAsString using implicit Intents.
     *
     * @param v Button that was clicked.
     */
    public void onClickOpenReviewsWebapge(View v) {

        //clickedFilmObj.getReviewURL().clear(); // <= just to test Toast
        if (clickedFilmObj.getReviewURL().isEmpty()) {
            Toast.makeText(ChildActivity.this, getResources().getString(R.string.no_reviews), Toast.LENGTH_SHORT).show();
        } else {
            String urlAsString = clickedFilmObj.getReviewURL();
            openWebPage(urlAsString);
        }

    }


    /**
     * This method fires off an implicit Intent to open a webpage.
     * Code from T04b.01
     * Also ref. https://developer.android.com/guide/components/intents-common#Browser
     *
     * @param url Url of webpage to open. Should start with http:// or https:// as that is the
     *            scheme of the URI expected with this Intent according to the Common Intents page
     */
    public void openWebPage(String url) {

        Uri webpage = Uri.parse(url);
        /*
         * Here, we create the Intent with the action of ACTION_VIEW. This action allows the user
         * to view particular content. In this case, our webpage URL.
         */
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);

        /*
         * This is a check we perform with every implicit Intent that we launch. In some cases,
         * the device where this code is running might not have an Activity to perform the action
         * with the data we've specified. Without this check, in those cases your app would crash.
         */
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

}