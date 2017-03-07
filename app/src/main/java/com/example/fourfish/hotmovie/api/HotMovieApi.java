package com.example.fourfish.hotmovie.api;

import com.example.fourfish.hotmovie.adapter.ListMovieIfo;
import com.example.fourfish.hotmovie.adapter.MovieInfo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by fourfish on 2017/3/4.
 */

public interface HotMovieApi {
    String BASE_URL="http://api.themoviedb.org/3/";

    @GET("movie/{type}")
    Call<ListMovieIfo<MovieInfo>> movieList(@Path("type") String type,
                                            @Query("language") String lan,
                                            @Query("api_key") String apiKey);
}
