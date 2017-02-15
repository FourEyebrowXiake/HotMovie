package com.example.fourfish.hotmovie;

/**
 * Created by fourfish on 2017/2/15.
 */

public class Movie {
    private String pictureUrl;
    private String name;
    private String movieGrade;
    private String movieIntro;
    private String movieTime;
    private String id;

    public String getString(){
        return getName()+" "+getMovieTime()+" "+getMovieGrade()+" "+getMovieIntro();
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
}
