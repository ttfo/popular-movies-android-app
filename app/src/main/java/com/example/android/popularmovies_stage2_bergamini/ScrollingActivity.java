package com.example.android.popularmovies_stage2_bergamini;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.example.android.popularmovies_stage2_bergamini.model.AppDatabase;
import com.example.android.popularmovies_stage2_bergamini.model.Film;
import com.example.android.popularmovies_stage2_bergamini.model.MainViewModel;
import com.example.android.popularmovies_stage2_bergamini.utils.JsonUtils;
import com.example.android.popularmovies_stage2_bergamini.utils.NetworkUtils;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * REQUIREMENTS FOR STAGE 1
 * This app will:
 * Present the user with a grid arrangement of movie posters upon launch.
 * Allow the user to change sort order via a setting:
 * The sort order can be by most popular or by highest-rated
 * Allow the user to tap on a movie poster and transition to a details screen with additional information such as:
 *      -original title
 *      -movie poster image thumbnail
 *      -a plot synopsis (called overview in the api)
 *      -user rating (called vote_average in the api)
 *      release date
 *
 * REQUIREMENTS FOR STAGE 2
 * You’ll allow users to view and play trailers (either in the youtube app or a web browser). <= IMPLICIT INTENT
 *      -- EXAMPLE OF ENDPOINT URL: https://api.themoviedb.org/3/movie/550/videos?api_key=<INSERT_KEY_HERE>&language=en-US
 * You’ll allow users to read reviews of a selected movie. <= IMPLICIT INTENT
 *      -- EXAMPLE OF ENDPOINT URL: https://api.themoviedb.org/3/movie/550/reviews?api_key=<INSERT_KEY_HERE>&language=en-US
 * You’ll also allow users to mark a movie as a favorite in the details view by tapping a button (star).
 * You'll make use of Android Architecture Components (Room, LiveData, ViewModel and Lifecycle) to create a robust an efficient application.
 * You'll create a database using Room to store the names and ids of the user's favorite movies (and optionally, the rest of the information needed to display their favorites collection while offline).
 * You’ll modify the existing sorting criteria for the main view to include an additional pivot to show their favorites collection.
 *
 **/


public class ScrollingActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener, Serializable {

    MyRecyclerViewAdapter adapter;
    Context mContext;
    String API_KEY = null;

    ArrayList<String> posterURLData = new ArrayList<>(); // var used to store poster URL's as strings
    ArrayList<Film> data = new ArrayList<>(); // var used to store Film objects in our popular movie list

    List<Film> favData = new ArrayList<>(); // var used to store Film objects that were flagged as favourite by the user

    String actionSortFlag = ""; // var used to store latest status of sorting of choice

    // ONLY USED FOR TESTING
    // List<String> dataPlaceholderValues = Arrays.asList( "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20" ); // initialize data with placeholders

    // Create a ProgressBar variable to store a reference to the ProgressBar
    private ProgressBar mLoadingIndicator;

    // Create a variable to store a reference to the error message TextView
    private TextView mErrorMessageDisplay;

    // Create a variable to store a reference to the RecyclerView where film posters will appear
    private RecyclerView recyclerView;

    // Create a variable to store a reference to the image view where the movie posters are displayed
    private ImageView posterDisplay;

    // Member variable for the Database
    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = ScrollingActivity.this;
        API_KEY = mContext.getResources().getString(R.string.API_KEY);
        setContentView(R.layout.activity_scrolling);

        // Get a reference to the RecyclerView using findViewById
        recyclerView = findViewById(R.id.rv_movieList);

        // Get a reference to the ProgressBar using findViewById
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        // Get a reference to the error TextView using findViewById
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        mDb = AppDatabase.getInstance(getApplicationContext());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            // reverse sorting upon button click
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                Log.i("MENU_SELECTION","Menu selection flag: " + actionSortFlag);
                if (actionSortFlag.equals("f")) { // menu selection is 'fav movies'
                    setupFavMoviesVM();
                    Snackbar.make(view, "Unordered list", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Collections.reverse(data);
                    Snackbar.make(view, "Order of list reversed!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    // re-load posters in UI grid after sorting
                    adapterSetUp(mContext, data);
                }
            }
        });

        // ONLY USED FOR TESTING
        // data to populate the RecyclerView with
        // REF. https://howtodoinjava.com/java/collections/arraylist/add-multiple-elements-arraylist/
        // posterURLData.addAll(dataPlaceholderValues);

        try {
            populateUI();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // set up the RecyclerView
        int numberOfColumns = 3;
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        adapterSetUp(this, data);
    }

    public void adapterSetUp(Context context, ArrayList<Film> myList) {
        adapter = new MyRecyclerViewAdapter(context, myList);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            /*
             * Switching between options on the menu
             */
            case R.id.action_sort_by_pop:

                // Resetting actionSortFlag, so to keep track of the latest choice
                actionSortFlag = mContext.getResources().getString(R.string.action_sort_flag_pop);
                Toast.makeText(getApplicationContext(), mContext.getResources().getString(R.string.action_sort_flag_pop_toast), Toast.LENGTH_SHORT).show();

                // Sorting with Java 8 lambda's
                // REF. https://stackoverflow.com/questions/4258700/collections-sort-with-multiple-fields
                // Collections.reverseOrder used to display higher rated movies at the top of screen
                // Ref. https://www.javatpoint.com/java-collections-reverseorder-method
                Collections.sort(data, Collections.reverseOrder (Comparator.comparing(Film::getPopularity)));

                // JUST FOR TESTING
//                for (Film film: data) {
//                    Log.i("SORTING", film.getOriginalTitle() + ", popularity: " + film.getPopularity());
//                }

                // Films are now ordered, need to reset posters in RecyclerView
                adapterSetUp(this, data);

                return true;

            case R.id.action_sort_by_rating:

                // Resetting actionSortFlag, so to keep track of the latest choice
                actionSortFlag = mContext.getResources().getString(R.string.action_sort_flag_rating);
                Toast.makeText(getApplicationContext(), mContext.getResources().getString(R.string.action_sort_flag_rating_toast), Toast.LENGTH_SHORT).show();

                // Sorting movies by rating; Collections.reverseOrder used to display higher rated movies at the top of screen
                // Ref. https://www.javatpoint.com/java-collections-reverseorder-method
                Collections.sort(data, Collections.reverseOrder (Comparator.comparing(Film::getVoteAverage)));

                // JUST FOR TESTING
//                for (Film film: data) {
//                    Log.i("SORTING", film.getOriginalTitle() + ", rating: " + film.getPopularity());
//                }

                // Films are now ordered, need to reset posters in RecyclerView
                adapterSetUp(this, data);

                return true;

            case R.id.action_show_fav:

                actionSortFlag = mContext.getResources().getString(R.string.action_sort_flag_fav);
                Toast.makeText(getApplicationContext(), mContext.getResources().getString(R.string.action_show_fav_toast), Toast.LENGTH_SHORT).show();

                setupFavMoviesVM();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(View view, int position) {

        Log.i("TAG", "You have clicked: " + adapter.getItem(position).getOriginalTitle() +
                " with popularity of " + adapter.getItem(position).getPopularity() +
                " and avg rating of " + adapter.getItem(position).getVoteAverage() +
                ", at cell position " + position + " in UI grid.");

        Intent childActivityIntent = new Intent(ScrollingActivity.this, ChildActivity.class);

        // REF. https://stackoverflow.com/questions/2736389/how-to-pass-an-object-from-one-activity-to-another-on-android
        childActivityIntent.putExtra(getResources().getString(R.string.film_object_label), (Serializable) adapter.getItem(position));

        startActivity(childActivityIntent);

    }

    private void populateUI() throws IOException {

        String lang = mContext.getResources().getString(R.string.MOVIE_DB_LANG);
        int pageNo = 1;

        PopularMovies pm = new PopularMovies(lang, pageNo);
        pm.execute(pm.getPopularMoviesURL());

        Log.i("PopularMoviesURL_1", pm.getPopularMoviesURL().toString());

        // Just some testing points
        //        ArrayList<Film> movieArrayList = pm.getPopularMoviesArrayList();
        //
        //        Log.i("PopularMoviesURL_2", movieArrayList.toString());

    }


    /**
     * This method will make the View for the JSON data visible and
     * hide the error message.
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showJsonDataView() {
        // First, make sure the error is invisible
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        // Then, make sure the JSON data is visible
        recyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide the JSON View.
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showErrorMessage() {
        // First, hide the currently visible data
        recyclerView.setVisibility(View.INVISIBLE);
        // Then, show the error
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    // loading fav movies via ViewModel
    private void setupFavMoviesVM() {

        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getFavMovies().observe(this, films -> {
            Log.d("TAG_DB_2", "Receiving DB update from LiveData");
            adapterSetUp(this, new ArrayList<Film>(films));
        });

    }


    public class PopularMovies extends AsyncTask<URL, Void, List<Film>> {

        private int pageNo;
        private String lang;
        URL popularMoviesURL;

        private String baseURL = mContext.getResources().getString(R.string.MOVIE_DB_BASE_URL);
        private String APIv = mContext.getResources().getString(R.string.MOVIE_DB_API_V);
        private String endPointPath = mContext.getResources().getString(R.string.POPULAR_MOVIES_ENDPOINT);
        private String endPointURL = baseURL + APIv + endPointPath;

        List<Film> popularMoviesList = new ArrayList<Film>();

        public PopularMovies(String lang, int pageNo) throws IOException {
            this.pageNo = pageNo;
            this.lang = lang;
            popularMoviesURL = NetworkUtils.popularMoviesBuilder(endPointURL, API_KEY, this.lang, this.pageNo);
        }

        public int getPageNo() {
            return pageNo;
        }

        public String getLang() {
            return lang;
        }

        public ArrayList<Film> getPopularMoviesArrayList() {
            return (ArrayList<Film>) popularMoviesList;
        }

        public URL getPopularMoviesURL() {
            return popularMoviesURL;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Film> doInBackground(URL... params) {
            URL popularMoviesURL = params[0];
            String popularMoviesString = null;

            try {
                popularMoviesString = NetworkUtils.getResponseFromHttpUrl(popularMoviesURL);
                Log.i("PopularMoviesURL_3", popularMoviesString);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                popularMoviesList = JsonUtils.parseFilmJson(popularMoviesString);
                Log.i("PopularMoviesURL_4", popularMoviesList.toString());
                Log.i("PopularMoviesURL_5", popularMoviesList.get(0).getOriginalTitle());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // TEST - trigger 'failed to get results' error
            // popularMoviesList.clear();
            // popularMoviesList = null;

            if (popularMoviesList == null) {

                // Alternative method to run showErrorMessage() here instead of running it 'onPostExecute'
                // REF. https://stackoverflow.com/questions/5161951/android-only-the-original-thread-that-created-a-view-hierarchy-can-touch-its-vi
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        showErrorMessage();
//                    }
//                });

                return popularMoviesList;

            } else {

                String posterBaseURL = mContext.getResources().getString(R.string.POSTER_BASE_URL);
                String imageSizeParam = mContext.getResources().getString(R.string.POSTER_IMAGE_SIZE);

                // Strings to help building the reviews and videos URLs for each movie
                String movieEndPointURL = baseURL + APIv + mContext.getResources().getString(R.string.MOVIE_ENDPOINT);
                String lang = mContext.getResources().getString(R.string.MOVIE_DB_LANG);
                String reviewsPath = mContext.getResources().getString(R.string.REVIEWS_PATH);
                String videoPath = mContext.getResources().getString(R.string.VIDEOS_PATH);

                // Populates UI with movies data
                for (Film film : popularMoviesList) {
                    // String filmPosterPath = film.getPosterPath();
                    String posterURLString = NetworkUtils.posterURLString(film, posterBaseURL, imageSizeParam);
                    Log.i("PopularMoviesURL_6", film.getOriginalTitle());
                    Log.i("PopularMoviesURL_7", film.getPosterPath());
                    Log.i("PopularMoviesURL_8", posterURLString);

                    posterURLData.add(posterURLString);
                    film.setPosterURLAsString(posterURLString);


                    URL reviewsURL = NetworkUtils.movieEndPointURLBuilder(movieEndPointURL, film.getId(), reviewsPath, API_KEY, lang);
                    URL videosURL = NetworkUtils.movieEndPointURLBuilder(movieEndPointURL, film.getId(), videoPath, API_KEY, lang);

                    String reviewsJSON = null;
                    String videosJSON = null;

                    try {
                        reviewsJSON = NetworkUtils.getResponseFromHttpUrl(reviewsURL);
                        Log.i("TAG_CheckReviewsURL", reviewsURL.toString());
                        Log.i("TAG_CheckReviewsURL", reviewsJSON);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (reviewsJSON != null) {
                        String reviewURL =null;
                        // parse JSON and extract review URL
                        try {
                            reviewURL = JsonUtils.reviewURLJsonExtractor(reviewsJSON);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        film.setReviewURL(reviewURL);
                        Log.i("TAG_CheckReviewsURL", "Review URL: " + reviewURL);
                    }

                    try {
                        videosJSON = NetworkUtils.getResponseFromHttpUrl(videosURL);
                        Log.i("TAG_CheckVideosURL", videosURL.toString());
                        Log.i("TAG_CheckVideosURL", videosJSON);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (videosJSON != null) {
                        String videoKey = null;
                        String videoURL = null;
                        // parse JSON and extract review URL
                        try {
                            videoKey = JsonUtils.trailerYTKeyJsonExtractor(videosJSON);
                            videoURL = NetworkUtils.youTubeURLBuilder(videoKey);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        film.setTrailerOnYouTubeURL(videoURL);
                        Log.i("TAG_CheckVideosURL", "YT Video URL: " + videoURL);
                    }

                    data.add(film);
                }

            }

            return popularMoviesList;
        }

        @Override
        protected void onPostExecute(List<Film> popularMoviesList) {
            // As soon as the loading is complete, hide the loading indicator
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (popularMoviesList != null) {
                if (!popularMoviesList.isEmpty()) {
                    // Call showJsonDataView if we have valid, non-null results
                    showJsonDataView();

                } else {
                    showErrorMessage();
                }
            } else {
                showErrorMessage();
            }
        }

    }


}