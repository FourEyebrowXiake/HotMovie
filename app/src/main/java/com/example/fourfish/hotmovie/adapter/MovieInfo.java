package com.example.fourfish.hotmovie.adapter;

import com.google.gson.annotations.SerializedName;

/**
 * Created by fourfish on 2017/3/4.
 */

public class MovieInfo {
    @SerializedName("poster_path")
    public String poster_path;

    @SerializedName("overview")
    public String overview;

    @SerializedName("release_date")
    public String release_date;

    @SerializedName("id")
    public int id;

    @SerializedName("title")
    public String title;

    @SerializedName("backdrop_path")
    public String backdrop_path;

    @SerializedName("vote_average")
    public double vote_average;

    @Override
    public String toString(){
        return " title: "+title+" id: "+id+" overview: "+overview;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public double getVote_average() {
        return vote_average;
    }

    public void setVote_average(double vote_average) {
        this.vote_average = vote_average;
    }
}
