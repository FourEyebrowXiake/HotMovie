package com.example.fourfish.hotmovie.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by fourfish on 2017/2/15.
 */

public class Movie implements Parcelable{
    private String pictureUrl;
    private String name;
    private String movieGrade;
    private String movieIntro;
    private String movieTime;
    private String id;

    public Movie(){

    }

    public Movie(Parcel in) {
        pictureUrl = in.readString();
        name = in.readString();
        movieGrade = in.readString();
        movieIntro = in.readString();
        movieTime = in.readString();
        id = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getString(){
        return getName()+" "+getMovieTime()+" "+getMovieGrade()+" "+getMovieIntro()+" "+getPictureUrl();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMovieGrade() {
        return movieGrade;
    }

    public void setMovieGrade(String movieGrade) {
        this.movieGrade = movieGrade;
    }

    public String getMovieIntro() {
        return movieIntro;
    }

    public void setMovieIntro(String movieIntro) {
        this.movieIntro = movieIntro;
    }

    public String getMovieTime() {
        return movieTime;
    }

    public void setMovieTime(String movieTime) {
        this.movieTime = movieTime;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(getId());
        parcel.writeString(getMovieGrade());
        parcel.writeString(getMovieIntro());
        parcel.writeString(getMovieTime());
        parcel.writeString(getName());
        parcel.writeString(getPictureUrl());
    }
}
