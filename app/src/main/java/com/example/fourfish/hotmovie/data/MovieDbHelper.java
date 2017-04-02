package com.example.fourfish.hotmovie.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.fourfish.hotmovie.data.HotMovieContract.MovieEntry;
import com.example.fourfish.hotmovie.data.HotMovieContract.PreferenceEntry;
import com.example.fourfish.hotmovie.data.HotMovieContract.ReviewEntry;

/**
 * Created by fourfish on 2017/3/5.
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION=12;

    static final String DATABASE_NAME="movie.db";

    public MovieDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_PREFERENCE_TABLE="CREATE TABLE "+PreferenceEntry.TABLE_NAME
                +" ("+PreferenceEntry._ID+" INTEGER PRIMARY KEY, "+
                PreferenceEntry.COLUMN_PREFERECNE_SETTING+" TEXT UNIQUE NOT NULL "+
                " );";

        final String SQL_CREATE_MOVIE_TABLE="CREATE TABLE "+ MovieEntry.TABLE_NAME
                +" ("+MovieEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                MovieEntry.COLUMN_LOC_KEY+" INTEGER NOT NULL, "+
                MovieEntry.COLUMN_BACKDROP_PATH+" TEXT NOT NULL, "+
                MovieEntry.COLUMN_COLLECT+" INTEGER DEFAULT 0, "+
                MovieEntry.COLUMN_ID+" INTEGER NOT NULL, "+
                MovieEntry.COLUMN_OVERVIEW+" TEXT NOT NULL, "+
                MovieEntry.COLUMN_RELEASE_DATE+" TEXT NOT NULL, "+
                MovieEntry.COLUMN_TITLE+" TEXT NOT NULL, "+
                MovieEntry.COLUMN_VIDEO_SOURCE+" TEXT NOT NULL, "+
                MovieEntry.COLUMN_VOTE_AVERAGE+" REAL NOT NULL, "+
                MovieEntry.COLUMN_POSTER_PATH+" TEXT NOT NULL, "+
                MovieEntry.COLUMN_RUN_TIME+" INTEGER NOT NULL, "+

                " FOREIGN KEY ("+MovieEntry.COLUMN_LOC_KEY+") REFERENCES " +
                PreferenceEntry.TABLE_NAME+" ("+PreferenceEntry._ID+"), "+
                "UNIQUE ("+MovieEntry.COLUMN_ID+", "+
                MovieEntry.COLUMN_LOC_KEY+") ON CONFLICT REPLACE);";

        final String SQL_CREATE_REVIEW_TABLE="CREATE TABLE "+ ReviewEntry.TABLE_NAME
                +" ("+ReviewEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                ReviewEntry.COLUMN_LOC_KEY+" INTEGER NOT NULL, "+
                ReviewEntry.COLUMN_CONTENT+" TEXT NOT NULL, "+
                ReviewEntry.COLUMN_AUTHOR+" TEXT NOT NULL, "+

                " FOREIGN KEY ("+ReviewEntry.COLUMN_LOC_KEY+") REFERENCES "+
                MovieEntry.TABLE_NAME+" ("+MovieEntry.COLUMN_ID+"));";
//                "UNIQUE ("+ ReviewEntry.COLUMN_LOC_KEY+") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_PREFERENCE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEW_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Note that this only fires if you change the version number for your database.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+PreferenceEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ReviewEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
