package com.example.fourfish.hotmovie.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.example.fourfish.hotmovie.data.HotMovieContract.MovieEntry.buildMovieUri;

/**
 * Created by fourfish on 2017/3/5.
 */

public class MovieProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher=buildUriMatcher();
    private MovieDbHelper mMovieDbHelper;

    static final int MOVIE=100;
    static final int MOVIE_WITH_PREFERENCE=101;
    static final int MOVIE_WITH_PREFER_AND_ID=102;
    static final int MOVIE_WITH_PREFER_AND_COLLECT=103;
    static final int PREFERENCE=200;
    static final int REVIEW=300;
    static final int REVIEW_WITH_MOVIE=301;

    private static final SQLiteQueryBuilder sMovieByPreferenceSettingQueryBuilder;

    private static final SQLiteQueryBuilder sReviewByMovieIdQueryBuilder;

    static{
        sMovieByPreferenceSettingQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sMovieByPreferenceSettingQueryBuilder.setTables(
                HotMovieContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
                        HotMovieContract.PreferenceEntry.TABLE_NAME +
                        " ON " + HotMovieContract.MovieEntry.TABLE_NAME +
                        "." + HotMovieContract.MovieEntry.COLUMN_LOC_KEY +
                        " = " + HotMovieContract.PreferenceEntry.TABLE_NAME +
                        "." + HotMovieContract.PreferenceEntry._ID);
    }

    static {
        sReviewByMovieIdQueryBuilder=new SQLiteQueryBuilder();

        sReviewByMovieIdQueryBuilder.setTables(
                HotMovieContract.ReviewEntry.TABLE_NAME+" INNER JOIN " +
                        HotMovieContract.MovieEntry.TABLE_NAME+
                        " ON " +HotMovieContract.ReviewEntry.TABLE_NAME+
                        "."+HotMovieContract.ReviewEntry.COLUMN_LOC_KEY+
                        " = "+HotMovieContract.MovieEntry.TABLE_NAME+
                        "."+HotMovieContract.MovieEntry.COLUMN_ID
        );
    }


    //preference.preference_setting = ?
    private static final String sPreferenceSettingSeleciton=
            HotMovieContract.PreferenceEntry.TABLE_NAME+
                    "."+HotMovieContract.PreferenceEntry.COLUMN_PREFERECNE_SETTING+" = ? ";

    //movie.id = ?
    private static final String sMovieIdSelection=
            HotMovieContract.MovieEntry.TABLE_NAME+
                    "."+HotMovieContract.MovieEntry.COLUMN_ID+" = ? ";

    //preference.preference_setting = ? AND isCollect = ?
    private static final String sMoviePreferenceAndDaySelection=
            HotMovieContract.PreferenceEntry.TABLE_NAME+
                    "."+HotMovieContract.PreferenceEntry.COLUMN_PREFERECNE_SETTING
                    + " = ? AND " +HotMovieContract.MovieEntry.COLUMN_COLLECT
                    + " = ? ";

    private Cursor getMovieByPreferenceSetting(Uri uri,String[] projection,String sortOrder){
        String preferenceSetting=HotMovieContract.MovieEntry.getPreferenceFromUri(uri);
        
        String[] selectionArgs;
        String selection;
        
        selection=sPreferenceSettingSeleciton;
        selectionArgs=new String[]{preferenceSetting};
        
        return sMovieByPreferenceSettingQueryBuilder.query(mMovieDbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    private Cursor getMovieByPreferenceandCollect(
       Uri uri,String[] projection,String sortOrder){
        long id=HotMovieContract.MovieEntry.getIdFromUri(uri);
        String preference=HotMovieContract.MovieEntry.getPreferenceFromUri(uri);

        return sMovieByPreferenceSettingQueryBuilder.query(mMovieDbHelper.getReadableDatabase(),
                projection,
                sMoviePreferenceAndDaySelection,
                new String[]{preference,Long.toString(id)},
                null,
                null,
                sortOrder);
    }

    private Cursor getReviewByMovieId(Uri uri,String[] projection,String sortOrder){
        String movieId=HotMovieContract.ReviewEntry.getMovieIdFromUri(uri);

        String[] selectionArgs;
        String selection;

        selection=sMovieIdSelection;
        selectionArgs=new String[]{movieId};

        return sReviewByMovieIdQueryBuilder.query(mMovieDbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    @Override
    public boolean onCreate() {
        mMovieDbHelper=new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor = null;
        switch (sUriMatcher.match(uri)) {
            case MOVIE_WITH_PREFER_AND_COLLECT:
            {
                retCursor=getMovieByPreferenceandCollect(uri,projection,sortOrder);
                break;
            }
            case MOVIE_WITH_PREFERENCE:
                retCursor = getMovieByPreferenceSetting(uri, projection, sortOrder);
                break;
            case MOVIE:
                retCursor = mMovieDbHelper.getReadableDatabase().query(
                        HotMovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case PREFERENCE:
                retCursor = mMovieDbHelper.getReadableDatabase().query(
                        HotMovieContract.PreferenceEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case REVIEW_WITH_MOVIE:
                Log.i("REVIEW_WITH_MOVIE","ARRIVE!");
                retCursor = getReviewByMovieId(uri, projection, sortOrder);
                break;
            case REVIEW:
                retCursor = mMovieDbHelper.getReadableDatabase().query(
                        HotMovieContract.ReviewEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        final int match=sUriMatcher.match(uri);

        switch (match){
            case MOVIE_WITH_PREFER_AND_COLLECT:
                return HotMovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case MOVIE_WITH_PREFERENCE:
                return HotMovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE:
                return HotMovieContract.MovieEntry.CONTENT_TYPE;
            case PREFERENCE:
                return HotMovieContract.PreferenceEntry.CONTENT_TYPE;
            case REVIEW:
                return HotMovieContract.ReviewEntry.CONTENT_TYPE;
            case REVIEW_WITH_MOVIE:
                return HotMovieContract.ReviewEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db=mMovieDbHelper.getWritableDatabase();
        final int match=sUriMatcher.match(uri);
        Uri returnUri;

        switch (match){
            case MOVIE: {
                long _id = db.insert(HotMovieContract.MovieEntry.TABLE_NAME, null, contentValues);
                if (_id > 0) {
                    returnUri = buildMovieUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case REVIEW: {
                long _id = db.insert(HotMovieContract.ReviewEntry.TABLE_NAME, null, contentValues);
                if (_id > 0) {
                    returnUri = HotMovieContract.ReviewEntry.buildReviewUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case PREFERENCE:{
                long _id = db.insert(HotMovieContract.PreferenceEntry.TABLE_NAME, null, contentValues);
                if (_id > 0) {
                    returnUri = HotMovieContract.PreferenceEntry.buildPreferenceUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db=mMovieDbHelper.getWritableDatabase();
        final int match=sUriMatcher.match(uri);
        int rowsDeleted;

        if (null==selection) selection="1";
        switch (match){
            case MOVIE:
                rowsDeleted = db.delete(HotMovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REVIEW:
                rowsDeleted=db.delete(HotMovieContract.ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        final SQLiteDatabase db=mMovieDbHelper.getWritableDatabase();
        final int match=sUriMatcher.match(uri);
        int rowsUpdated;
        switch (match){
            case MOVIE:
                rowsUpdated=db.update(HotMovieContract.MovieEntry.TABLE_NAME,contentValues,s,strings);
                break;
            case REVIEW:
                rowsUpdated=db.update(HotMovieContract.ReviewEntry.TABLE_NAME,contentValues,s,strings);
                break;
            case REVIEW_WITH_MOVIE:
                rowsUpdated=db.update(HotMovieContract.MovieEntry.TABLE_NAME,contentValues,s,strings);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match){
            case MOVIE: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(HotMovieContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            case REVIEW: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(HotMovieContract.ReviewEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
             default:
                 return super.bulkInsert(uri, values);
        }
    }


    static UriMatcher buildUriMatcher(){
        final UriMatcher matcher=new UriMatcher(UriMatcher.NO_MATCH);
        final String authority=HotMovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, HotMovieContract.PATH_MOVIE,MOVIE);
        matcher.addURI(authority,HotMovieContract.PATH_MOVIE+"/*",MOVIE_WITH_PREFERENCE);
        matcher.addURI(authority,HotMovieContract.PATH_MOVIE+"/*/#",MOVIE_WITH_PREFER_AND_COLLECT);
        matcher.addURI(authority,HotMovieContract.PATH_MOVIE+"/*/*",MOVIE_WITH_PREFER_AND_COLLECT);

        matcher.addURI(authority,HotMovieContract.PATH_PREFERENCE,PREFERENCE);

        matcher.addURI(authority,HotMovieContract.PATH_REVIEW,REVIEW);
        matcher.addURI(authority,HotMovieContract.PATH_REVIEW+"/#",REVIEW_WITH_MOVIE);
        matcher.addURI(authority,HotMovieContract.PATH_REVIEW+"/*",REVIEW_WITH_MOVIE);
        return matcher;
    }




    @Override
    @TargetApi(11)
    public void shutdown() {
        mMovieDbHelper.close();
        super.shutdown();
    }

}
