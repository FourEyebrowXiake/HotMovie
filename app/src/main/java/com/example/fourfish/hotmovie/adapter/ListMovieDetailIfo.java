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

    @SerializedName("id")
    public int mId;

    @SerializedName("vote_average")
    public double mGrade;

    @SerializedName("runtime")
    public int mRuntime;

    public int getRuntime() {
        return mRuntime;
    }

    public void setRuntime(int runtime) {
        mRuntime = runtime;
    }

    public double getGrade() {
        return mGrade;
    }

    public void setGrade(double grade) {
        mGrade = grade;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public static class trailers<Youtube>{
        @SerializedName("youtube")
        public List<Youtube> mYoutubeList;
    }

    public static class reviews<Review>{
        @SerializedName("results")
        public List<Review> mReviewList;
    }

}
