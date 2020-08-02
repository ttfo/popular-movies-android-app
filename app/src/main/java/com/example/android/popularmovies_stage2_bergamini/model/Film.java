package com.example.android.popularmovies_stage2_bergamini.model;

// *** JSON EXAMPLE FROM themoviedb.org ***
//{
//        "adult": false,
//        "backdrop_path": "/nlCHUWjY9XWbuEUQauCBgnY8ymF.jpg",
//        "belongs_to_collection": {
//        "id": 8945,
//        "name": "Mad Max Collection",
//        "poster_path": "/9U9QmbCDIBhqDShuIxOiS9gjKYz.jpg",
//        "backdrop_path": "/zI0q2ENcQOLECbe0gAEGlncVh2j.jpg"
//        },
//        "budget": 150000000,
//        "genres": [
//        {
//        "id": 28,
//        "name": "Action"
//        },
//        {
//        "id": 12,
//        "name": "Adventure"
//        },
//        {
//        "id": 878,
//        "name": "Science Fiction"
//        }
//        ],
//        "homepage": "http://www.madmaxmovie.com",
//        "id": 76341,
//        "imdb_id": "tt1392190",
//        "original_language": "en",
//        "original_title": "Mad Max: Fury Road",
//        "overview": "An apocalyptic story set in the furthest reaches of our planet, in a stark desert landscape where humanity is broken, and most everyone is crazed fighting for the necessities of life. Within this world exist two rebels on the run who just might be able to restore order.",
//        "popularity": 28.82,
//        "poster_path": "/sQuxceRcV3cxKH5CAnAUZe0qFKS.jpg",
//        "production_companies": [
//        {
//        "id": 2537,
//        "logo_path": null,
//        "name": "Kennedy Miller Productions",
//        "origin_country": "AU"
//        },
//        {
//        "id": 174,
//        "logo_path": "/IuAlhI9eVC9Z8UQWOIDdWRKSEJ.png",
//        "name": "Warner Bros. Pictures",
//        "origin_country": "US"
//        },
//        {
//        "id": 41624,
//        "logo_path": "/wzKxIeQKlMP0y5CaAw25MD6EQmf.png",
//        "name": "RatPac-Dune Entertainment",
//        "origin_country": "US"
//        },
//        {
//        "id": 79,
//        "logo_path": "/tpFpsqbleCzEE2p5EgvUq6ozfCA.png",
//        "name": "Village Roadshow Pictures",
//        "origin_country": "US"
//        }
//        ],
//        "production_countries": [
//        {
//        "iso_3166_1": "AU",
//        "name": "Australia"
//        },
//        {
//        "iso_3166_1": "US",
//        "name": "United States of America"
//        },
//        {
//        "iso_3166_1": "ZA",
//        "name": "South Africa"
//        }
//        ],
//        "release_date": "2015-05-13",
//        "revenue": 378858340,
//        "runtime": 121,
//        "spoken_languages": [
//        {
//        "iso_639_1": "en",
//        "name": "English"
//        }
//        ],
//        "status": "Released",
//        "tagline": "What a Lovely Day.",
//        "title": "Mad Max: Fury Road",
//        "video": false,
//        "vote_average": 7.5,
//        "vote_count": 16684
//        }

// *** REQUIREMENTS ***
//  *      -original title
//  *      -movie poster image thumbnail
//  *      -a plot synopsis (called overview in the api)
//  *      -user rating (called vote_average in the api)
//  *      -release date

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.net.URL;
import java.util.List;

@Entity(tableName = "fav_movies") // ROOM annotation; setting up table to store user's favourite movies
public class Film implements Serializable {

    //public static Comparator<? super Film> popularity;
    @PrimaryKey // ROOM annotation
    private Integer id;
    @ColumnInfo(name = "original_title") // ROOM annotation
    private String originalTitle;
    @ColumnInfo(name = "poster_path") // ROOM annotation
    private String posterPath;
    private String overview;
    private Integer popularity;
    @ColumnInfo(name = "vote_avg") // ROOM annotation
    private Integer voteAverage;
    @ColumnInfo(name = "release_date") // ROOM annotation
    private String releaseDate;
    @ColumnInfo(name = "poster_url") // ROOM annotation
    private String posterURLAsString;
    @ColumnInfo(name = "review_url") // ROOM annotation
    private String reviewURL;
    @ColumnInfo(name = "trailer_url") // ROOM annotation
    private String trailerOnYouTubeURL;
    @ColumnInfo(name = "fav_flag") // ROOM annotation
    private boolean isFavourite;


    /**
     * No args constructor for use in serialization
     */
    @Ignore // ROOM annotation
    public Film() {
    }

    public Film(Integer id, boolean isFavourite) {
        this.id = id;
        this.isFavourite = isFavourite;
    }

    @Ignore // ROOM annotation
    public Film(Integer id, String originalTitle, String posterPath, String overview, Integer popularity, Integer voteAverage,
                String releaseDate, String posterURLAsString, String reviewURL, String trailerOnYouTubeURL, boolean isFavourite) {
        this.id = id;
        this.originalTitle = originalTitle;
        this.posterPath = posterPath;
        this.overview = overview;
        this.popularity = popularity;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
        this.posterURLAsString = posterURLAsString;
        this.reviewURL = reviewURL;
        this.trailerOnYouTubeURL = trailerOnYouTubeURL;
        this.isFavourite = isFavourite;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public Integer getPopularity() {
        return popularity;
    }

    public void setPopularity(Integer popularity) {
        this.popularity = popularity;
    }

    public Integer getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Integer voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPosterURLAsString() {
        return posterURLAsString;
    }

    public void setPosterURLAsString(String posterURLAsString) {
        this.posterURLAsString = posterURLAsString;
    }

    public String getReviewURL() {
        return reviewURL;
    }

    public void setReviewURL(String reviewURL) {
        this.reviewURL = reviewURL;
    }

    public String getTrailerOnYouTubeURL() {
        return trailerOnYouTubeURL;
    }

    public void setTrailerOnYouTubeURL(String trailerOnYouTubeURL) {
        this.trailerOnYouTubeURL = trailerOnYouTubeURL;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

}
