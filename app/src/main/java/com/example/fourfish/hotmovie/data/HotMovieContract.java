package com.example.fourfish.hotmovie.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by fourfish on 2017/3/4.
 */

public class HotMovieContract {

    //Content authority
    public static final String CONTENT_AUTHORITY="com.example.fourfish.hotmovie.app";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE="movie";
    public static final String PATH_PREFERENCE="preference";
    public static final String PATH_REVIEW="review";

    public static final class PreferenceEntry implements BaseColumns{

        public static final Uri CONTETN_URI=BASE_CONTENT_URI.buildUpon().appendPath(PATH_PREFERENCE)
                .build();

        public static final String CONTENT_TYPE=
                ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_PREFERENCE;

        public static final String TABLE_NAME="preference";

        public static final String COLUMN_PREFERECNE_SETTING = "preference_setting";

        public static Uri buildPreferenceUri(long id){
            return ContentUris.withAppendedId(CONTETN_URI,id);
        }
    }

    public static final class MovieEntry implements BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = "movie";

        //foreign key
        public static final String COLUMN_LOC_KEY="preference_id";

        public static final String COLUMN_POSTER_PATH="poster_path";

        public static final String COLUMN_OVERVIEW="overview";

        public static final String COLUMN_RELEASE_DATE="release_date";

        public static final String COLUMN_TITLE="title";

        public static final String COLUMN_BACKDROP_PATH="backdrop_path";

        public static final String COLUMN_VOTE_AVERAGE="vote_average";

        public static final String COLUMN_ID="id";

        public static final String COLUMN_VIDEO_SOURCE="video_source";

        //to determine whether the movie is collected
        public static final String COLUMN_COLLECT="is_collect";

        public static final String COLUMN_RUN_TIME="runtime";


        public static Uri buildMovieUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }

        public static Uri buildMoviePreference(String preference){
            return CONTENT_URI.buildUpon().appendPath(preference).build();
        }

        public static Uri buildMoviePreferencWithId(String preference,long id){
            return CONTENT_URI.buildUpon().appendPath(preference).
                    appendPath(Long.toString(id)).build();
        }

        public static Uri buildMoviePreferenceIfCollect(String preference,int isCollect){
            return CONTENT_URI.buildUpon().appendPath(preference).appendPath(Integer.toString(isCollect))
                    .build();
        }

        public static String getPreferenceFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }


        public static long getIdFromUri(Uri uri){
            return Long.parseLong(uri.getPathSegments().get(2));
        }
    }

    public static final class ReviewEntry implements BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;

        public static final String TABLE_NAME="review";

        public static final String COLUMN_LOC_KEY="movie_id";

        public static final String COLUMN_AUTHOR="author";

        public static final String COLUMN_CONTENT="content";

        public static Uri buildReviewUri(Long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }

        public static Uri buildReviewWithMovie(long movieId){
            return CONTENT_URI.buildUpon().appendPath(Long.toString(movieId)).build();
        }

        public static String getMovieIdFromUri(Uri uri){
            return  uri.getPathSegments().get(1);
        }
    }
}
