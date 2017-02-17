package com.example.fourfish.hotmovie.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fourfish on 2017/2/15.
 */

public class MoviesInfor {

    List<Movie> mMovies;

    private static class MoviesInforHolder{

        public static MoviesInfor instance=new MoviesInfor();
    }

    public static MoviesInfor newInstance(){
        return MoviesInforHolder.instance;
    }

    public MoviesInfor(){
        mMovies=new ArrayList<>();
    }

    public void addMovieInfo(Movie movie){
        mMovies.add(movie);
    }

    public Movie getMovie(int position){
        return mMovies.get(position);
    }

    public void clearList(){
        mMovies.clear();
    }
}
