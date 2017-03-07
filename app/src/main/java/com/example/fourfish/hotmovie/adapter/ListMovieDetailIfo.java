package com.example.fourfish.hotmovie.adapter;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by fourfish on 2017/3/5.
 */

public class ListMovieDetailIfo {
    @SerializedName("trailers")
    public trailers<Youtube> mTrailers;

    @SerializedName("reviews")
    public reviews<Review> mReviews;

    public static class trailers<Youtube>{
        @SerializedName("youtube")
        public List<Youtube> mYoutubeList;
    }

    public static class reviews<Review>{
        @SerializedName("results")
        public List<Review> mReviewList;
    }

}
