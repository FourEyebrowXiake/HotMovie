package com.example.fourfish.hotmovie.api;

import com.example.fourfish.hotmovie.adapter.ListMovieDetailIfo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by fourfish on 2017/3/5.
 */

public interface DetailMovieApi {
    String BASE_URL="http://api.themoviedb.org/3/";
    @GET("movie/{movie_id}")
    Call<ListMovieDetailIfo> getDetailIfo(@Path("movie_id") String id,
                                          @Query("append_to_response") String type,
                                          @Query("api_key") String api_key);
}
