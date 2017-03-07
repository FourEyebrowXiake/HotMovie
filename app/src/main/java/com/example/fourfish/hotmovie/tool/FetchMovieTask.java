package com.example.fourfish.hotmovie.tool;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.fourfish.hotmovie.BuildConfig;
import com.example.fourfish.hotmovie.adapter.ListMovieDetailIfo;
import com.example.fourfish.hotmovie.adapter.ListMovieIfo;
import com.example.fourfish.hotmovie.adapter.MovieInfo;
import com.example.fourfish.hotmovie.adapter.Review;
import com.example.fourfish.hotmovie.adapter.Youtube;
import com.example.fourfish.hotmovie.api.DetailMovieApi;
import com.example.fourfish.hotmovie.api.HotMovieApi;
import com.example.fourfish.hotmovie.data.HotMovieContract;
import com.example.fourfish.hotmovie.data.HotMovieContract.MovieEntry;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by fourfish on 2017/2/16.
 */

public class FetchMovieTask extends AsyncTask<String, Void, List<String>> {

    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

    private HotMovieApi mHotMovieApi;
    private DetailMovieApi mDetailMovieApi;

    private String mSort;
    private int movieId;


    private Context mContext;

    public FetchMovieTask(Context ctx)
    {
        if (ctx!=null) {
            this.mContext = ctx;
        }
    }


    @Override
    protected List<String> doInBackground(String... params) {

        // If there's no zip code, there's nothing to look up.  Verify size of params.
        if (params.length == 0) {
            return null;
        }

        mSort=params[0];

        String language="zh";
        String append_to_response="trailers,reviews";

        try {

            createHotMovieApi();

            mHotMovieApi.movieList(mSort, language, BuildConfig.OPEN_WEATHER_MAP_API_KEY).enqueue(mListMovieIfoCallback);

            for (int i : getAllId()) {
                String id = i + "";
                movieId = i;
                mDetailMovieApi.getDetailIfo(id, append_to_response, BuildConfig.OPEN_WEATHER_MAP_API_KEY).enqueue(mListMovieDetailIfoCallback);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    private void  createHotMovieApi(){
        OkHttpClient.Builder httpClient=new OkHttpClient.Builder();
        Retrofit.Builder builder=new Retrofit.Builder()
                .baseUrl(HotMovieApi.BASE_URL)
                .addConverterFactory(
                        GsonConverterFactory.create()
                );
        Retrofit retrofit=builder.client(httpClient.build()).build();
        mHotMovieApi=retrofit.create(HotMovieApi.class);

        mDetailMovieApi=retrofit.create(DetailMovieApi.class);

    }

    long addPreference(String preference){
        long preferenceId=0;

        Cursor preferenceCursor=mContext.getContentResolver().query(
                HotMovieContract.PreferenceEntry.CONTETN_URI,
                new String[]{HotMovieContract.PreferenceEntry._ID},
                HotMovieContract.PreferenceEntry.COLUMN_PREFERECNE_SETTING+" = ?",
                new String[]{preference},
                null);

                if(preferenceCursor!=null) {

                    if (preferenceCursor.moveToFirst()) {
                        int preIndex = preferenceCursor.getColumnIndex(HotMovieContract.PreferenceEntry._ID);
                        preferenceId = preferenceCursor.getLong(preIndex);
                        preferenceCursor.close();
                    }

                }else {
                        ContentValues preValues = new ContentValues();

                        preValues.put(HotMovieContract.PreferenceEntry.COLUMN_PREFERECNE_SETTING, preference);

                        Uri insertedUri = mContext.getContentResolver().insert(
                                HotMovieContract.PreferenceEntry.CONTETN_URI,
                                preValues
                        );

                        preferenceId = ContentUris.parseId(insertedUri);
                    preferenceCursor.close();
                    }


        return preferenceId;
    }

    ArrayList<Integer> getAllId(){
        Cursor idCursor=mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                new String[]{MovieEntry.COLUMN_ID},
                null,
                null,
                null
        );
        ArrayList<Integer> integerArrayList=new ArrayList<>();

        if(idCursor!=null) {

            try {
                while (idCursor.moveToNext()) {
                    integerArrayList.add(idCursor.getInt(idCursor.getColumnIndex(MovieEntry.COLUMN_ID)));
                }
            } finally {
                idCursor.close();
            }
        }

        return integerArrayList;
    }


    retrofit2.Callback<ListMovieIfo<MovieInfo>> mListMovieIfoCallback=new
            retrofit2.Callback<ListMovieIfo<MovieInfo>>() {
                @Override
                public void onResponse(Call<ListMovieIfo<MovieInfo>> call, Response<ListMovieIfo<MovieInfo>> response) {
                    if(response.isSuccessful()){

                            ArrayList<ContentValues> arrayList = new ArrayList<>();
                            ListMovieIfo<MovieInfo> listMovieIfo = response.body();

                        try {
                            for (MovieInfo ifo : listMovieIfo.items) {
                                String poster_path = "http://image.tmdb.org/t/p/w185" + ifo.getPoster_path();
                                String backup_path = "http://image.tmdb.org/t/p/w185" + ifo.getBackdrop_path();
                                Log.i("FetchMovieTask",poster_path);
                                    ContentValues movieValues = new ContentValues();
                                    movieValues.put(MovieEntry.COLUMN_LOC_KEY, addPreference(mSort));
                                    movieValues.put(MovieEntry.COLUMN_BACKDROP_PATH, backup_path);
                                    movieValues.put(MovieEntry.COLUMN_ID, ifo.getId());
                                    movieValues.put(MovieEntry.COLUMN_OVERVIEW, ifo.getOverview());
                                    movieValues.put(MovieEntry.COLUMN_POSTER_PATH, poster_path);
                                    movieValues.put(MovieEntry.COLUMN_TITLE, ifo.getTitle());
                                    movieValues.put(MovieEntry.COLUMN_VOTE_AVERAGE, ifo.getVote_average());
                                    movieValues.put(MovieEntry.COLUMN_RELEASE_DATE, ifo.getRelease_date());
                                    movieValues.put(MovieEntry.COLUMN_VIDEO_SOURCE,"");

                                    Log.i("FetchMovieTask:",movieValues.toString());
                                    arrayList.add(movieValues);
                            }
                                Log.i("FetchMovieTask:",arrayList.size()+"");
                            int inserted=0;
                            if (arrayList.size() > 0) {
                                ContentValues[] cvArray = new ContentValues[arrayList.size()];
                                arrayList.toArray(cvArray);
                                Log.i("FetchMovieTask:",cvArray[0].toString());
                                inserted=mContext.getContentResolver().bulkInsert(MovieEntry.CONTENT_URI,cvArray);
                            }
                        Log.d(LOG_TAG, "FetchMovie Complete. " + inserted + " Inserted");
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }else {
                        Log.d("ListMovieIfoCallback","Code:"+response.code()+
                                "  Message: "+response.message());
                    }
                }
                @Override
                public void onFailure(Call<ListMovieIfo<MovieInfo>> call, Throwable t) {
                    t.printStackTrace();
                }
            };


    retrofit2.Callback<ListMovieDetailIfo> mListMovieDetailIfoCallback=new
            Callback<ListMovieDetailIfo>() {
                @Override
                public void onResponse(Call<ListMovieDetailIfo> call, Response<ListMovieDetailIfo> response) {
                    if(response.isSuccessful()){
                        ListMovieDetailIfo listDetail=response.body();

                        ArrayList<ContentValues> vedioList=new ArrayList<>();
                        ArrayList<ContentValues> reviewList=new ArrayList<>();

                        try {

                            for (Youtube yotube : listDetail.mTrailers.mYoutubeList) {
                                ContentValues youtubeValues = new ContentValues();
                                String path = "https://www.youtube.com/watch?v=" + yotube.getSource();
                                youtubeValues.put(MovieEntry.COLUMN_VIDEO_SOURCE, path);
                                vedioList.add(youtubeValues);
                            }

                            for (Review review : listDetail.mReviews.mReviewList) {
                                ContentValues reviewValues = new ContentValues();
                                reviewValues.put(HotMovieContract.ReviewEntry.COLUMN_LOC_KEY, movieId);
                                reviewValues.put(HotMovieContract.ReviewEntry.COLUMN_AUTHOR, review.getAuthor());
                                reviewValues.put(HotMovieContract.ReviewEntry.COLUMN_CONTENT, review.getContent());
                                reviewList.add(reviewValues);
                            }

                            if (vedioList.size() > 0) {
                                ContentValues[] cvArray = new ContentValues[vedioList.size()];
                                vedioList.toArray(cvArray);
                                for (ContentValues content : cvArray) {
                                    mContext.getContentResolver().update(MovieEntry.CONTENT_URI, content, MovieEntry.COLUMN_VIDEO_SOURCE, null);
                                }
                            }

                            if (reviewList.size() > 0) {
                                ContentValues[] cvArray = new ContentValues[reviewList.size()];
                                reviewList.toArray(cvArray);
                                mContext.getContentResolver().bulkInsert(HotMovieContract.ReviewEntry.CONTENT_URI, cvArray);
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }

                    }else {
                        Log.d("ListMovieDetailCallback","Code:"+response.code()+
                                "  Message: "+response.message());
                    }
                }

                @Override
                public void onFailure(Call<ListMovieDetailIfo> call, Throwable t) {
                    t.printStackTrace();
                }
            };
}
