package com.example.android.popularmovies_stage2_bergamini.utils;

import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.example.android.popularmovies_stage2_bergamini.model.Film;


public class NetworkUtils extends AppCompatActivity {


    // This method builds the URL of the poster image provided by themoviedb.org
    // Example of URL:
    //      * BASE URL: http://image.tmdb.org/t/p/
    //      * IMAGE SIZE: w185
    //      * IMAGE PATH: /sQuxceRcV3cxKH5CAnAUZe0qFKS.jpg
    //      * FULL URL: http://image.tmdb.org/t/p/w185/sQuxceRcV3cxKH5CAnAUZe0qFKS.jpg
    public static String posterURLString(Film film, String baseURL, String imageSize) {

        String posterURL = null;

        // About StringBuilder, REF.: https://www.youtube.com/watch?v=AdjONxfokvk
        StringBuilder posterURLBuilder = new StringBuilder();

//      imageSize = getResources().getString(R.string.POSTER_IMAGE_SIZE);
//      String baseURL = "http://image.tmdb.org/t/p/";

        String posterPath = film.getPosterPath();

        if (posterPath != null) {
//            posterURL = baseURL + imageSize + posterPath;
            posterURLBuilder.append(baseURL)
                    .append(imageSize)
                    .append(posterPath);
        }

        posterURL = posterURLBuilder.toString();

        return posterURL;

    }

    // This method builds the URL of the poster
    public static URL posterURL(String posterURLString) {

        Uri builtUri = Uri.parse(posterURLString);
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;

    }

    // This method builds the URL of the list of popular movies
    public static URL popularMoviesBuilder(String endPointURL, String apiKey, String lang, int pageNo) {

//        String endPointURL = baseURL + APIv + endPointPath;

        final String KEY_QUERY = "api_key";
        final String LANG_QUERY = "language";
        final String PAGE_QUERY = "page";

        Uri builtUri = Uri.parse(endPointURL).buildUpon()
                .appendQueryParameter(KEY_QUERY, apiKey)
                .appendQueryParameter(LANG_QUERY, lang)
                .appendQueryParameter(PAGE_QUERY, String.valueOf(pageNo))
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;

    }

    /**
     * This method returns the entire result from the HTTP response.
     * REF. Exercise T02.06
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }


    // This method builds the URL of reviews or videos endpoint given the movie ID as param
    // Param movieEndPointPath can be either "reviews" or "videos"
    // Example of end-point URL for reviews: https://api.themoviedb.org/3/movie/550/reviews?api_key=<API_KEY>&language=en-US
    // Example of end-point URL for videos: https://api.themoviedb.org/3/movie/550/videos?api_key=<API_KEY>&language=en-US
    public static URL movieEndPointURLBuilder(String movieEndPointURL, int movieID, String movieEndPointPath, String apiKey, String lang) {

        final String KEY_QUERY = "api_key";
        final String LANG_QUERY = "language";

        String movieIDStr = String.valueOf(movieID);

        Uri builtUri = Uri.parse(movieEndPointURL).buildUpon()
                .appendPath(movieIDStr)
                .appendPath(movieEndPointPath)
                .appendQueryParameter(KEY_QUERY, apiKey)
                .appendQueryParameter(LANG_QUERY, lang)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;

    }


    // This method builds the URL of the trailer video on YouTube
    // Example of video URL: https://www.youtube.com/watch?v=qtRKdVHc-cE
    public static String youTubeURLBuilder(String videoKey) {

        final String YT_BASE_URL = "https://www.youtube.com";
        final String VIDEO_PATH = "watch";
        final String VIDEO_QUERY = "v";

        Uri builtUri = Uri.parse(YT_BASE_URL).buildUpon()
                .appendPath(VIDEO_PATH)
                .appendQueryParameter(VIDEO_QUERY, videoKey)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url.toString();

    }

}
