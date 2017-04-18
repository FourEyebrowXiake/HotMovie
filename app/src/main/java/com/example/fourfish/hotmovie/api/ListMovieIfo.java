package com.example.fourfish.hotmovie.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by fourfish on 2017/3/4.
 */

public class ListMovieIfo<T> {
    @SerializedName("page")
    public int page;

    @SerializedName("results")
    public List<T> items;
}
