Popular Movies app built (from scratch) in July 2020 as part of the UDACITY Android Developer Nanodegree.

Data is consumed via the API provided by themoviedb.org.
NOTE! To demo the app, replace value of String "API_KEY" in in app/src/main/res/values/strings.xml with your own API key.

To get an API key, register an account on themoviedb.org and follow steps at:
https://developers.themoviedb.org/3/getting-started/introduction

The app is showcasing the usage of Android layouts, explicit and implicit intents, AsyncTask in RecyclerView, JSON parsing and URL builder methods to retrieve and parse data from API endpoints and Android Architecture Components (Room, LiveData, ViewModel) to store, observe and cache data.

Original requirements as follows:

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
 *      -release date
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
