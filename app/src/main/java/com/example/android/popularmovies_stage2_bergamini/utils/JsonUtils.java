package com.example.android.popularmovies_stage2_bergamini.utils;

import android.util.Log;

import com.example.android.popularmovies_stage2_bergamini.model.Film;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    static String JSON_ARRAY_NAME = "results"; // name of list in JSON
    static String VIDEO_PROVIDER_YOUTUBE = "YouTube";
    static String VIDEO_TYPE_TRAILER = "Trailer";

    public static List<Film> parseFilmJson(String json) throws JSONException {

        List<Film> popularMoviesList = new ArrayList<Film>();

        JSONObject movieListObjJson = new JSONObject(json); // large string with movie list that needs parsing
        JSONArray listOfMoviesJson = movieListObjJson.getJSONArray(JSON_ARRAY_NAME);

        for (int i = 0; i < listOfMoviesJson.length(); i++){

            JSONObject filmInList = listOfMoviesJson.getJSONObject(i);
            Film film = new Film(); // create a new film object for each element in the array list

            film.setId(filmInList.getInt("id"));
            film.setOriginalTitle(filmInList.getString("original_title"));
            film.setPosterPath(filmInList.getString("poster_path"));
            film.setOverview(filmInList.getString("overview"));
            film.setPopularity(filmInList.getInt("popularity"));
            film.setVoteAverage(filmInList.getInt("vote_average"));
            film.setReleaseDate(filmInList.getString("release_date"));

            popularMoviesList.add(film);

        }

        return popularMoviesList;

    }

    // Method to extract review URL from themoviedb.org
    // Example of end-point https://api.themoviedb.org/3/movie/550/reviews?api_key=<API_KEY>&language=en-US
    public static String reviewURLJsonExtractor(String json) throws JSONException, MalformedURLException {

        List<String> reviewURLsList = new ArrayList<String>();
        String reviewURLAsString = null;

        JSONObject reviewsListObjJson = new JSONObject(json); // string with reviews list that needs parsing
        JSONArray listOfReviewsJson = reviewsListObjJson.getJSONArray(JSON_ARRAY_NAME);

        for (int i = 0; i < listOfReviewsJson.length(); i++){

            JSONObject reviewInList = listOfReviewsJson.getJSONObject(i);
            String reviewURL = reviewInList.getString("url");
            reviewURLsList.add(reviewURL);

        }

        if (reviewURLsList.size() > 0) {
            reviewURLAsString = reviewURLsList.get(0);
        }

        return reviewURLAsString;
    }

    // Method to extract Youtube key of movie trailer from themoviedb.org
    // Example of end-point https://api.themoviedb.org/3/movie/550/videos?api_key=<API_KEY>&language=en-US
    public static String trailerYTKeyJsonExtractor(String json) throws JSONException {

        List<String> movieVideosList = new ArrayList<String>();
        String trailerURLAsString = null;

        JSONObject videosListObjJson = new JSONObject(json); // string with videos list that needs parsing
        JSONArray listOfVideosJson = videosListObjJson.getJSONArray(JSON_ARRAY_NAME);

        for (int i = 0; i < listOfVideosJson.length(); i++){

            JSONObject videoInList = listOfVideosJson.getJSONObject(i);
            if ( videoInList.getString("site").equals(VIDEO_PROVIDER_YOUTUBE)
                && videoInList.getString("type").equals(VIDEO_TYPE_TRAILER)) {

                String YTKey = videoInList.getString("key");
                movieVideosList.add(YTKey);

            }

        }
        Log.i("TAG_Videos_JSONUTILS", movieVideosList.toString());

        if (movieVideosList.size() > 0) {
            trailerURLAsString = movieVideosList.get(0);
        }
        return trailerURLAsString;
    }

}
