package com.example.fourfish.hotmovie.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.example.fourfish.hotmovie.BuildConfig;
import com.example.fourfish.hotmovie.MainActivity;
import com.example.fourfish.hotmovie.R;
import com.example.fourfish.hotmovie.api.DetailMovieApi;
import com.example.fourfish.hotmovie.api.HotMovieApi;
import com.example.fourfish.hotmovie.api.ListMovieDetailIfo;
import com.example.fourfish.hotmovie.api.ListMovieIfo;
import com.example.fourfish.hotmovie.api.MovieInfo;
import com.example.fourfish.hotmovie.api.Review;
import com.example.fourfish.hotmovie.api.Youtube;
import com.example.fourfish.hotmovie.data.HotMovieContract;
import com.example.fourfish.hotmovie.data.HotMovieContract.MovieEntry;
import com.example.fourfish.hotmovie.tool.SharedPreferencesUtil;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class HotmovieSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = HotmovieSyncAdapter.class.getSimpleName();

    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    private static final int MOVIE_NOTIFICATION_ID = 3004;

    private HotMovieApi mHotMovieApi;
    private DetailMovieApi mDetailMovieApi;

    private long preferenceId;
    private Context mContext;

    private static final String[] MOVIE_COLUMNS = {
            HotMovieContract.MovieEntry.TABLE_NAME + "." + HotMovieContract.MovieEntry._ID,

            HotMovieContract.MovieEntry.COLUMN_OVERVIEW,
            HotMovieContract.MovieEntry.COLUMN_TITLE,
    };

    private static final int COL_MOVIE_OVERVIEW = 1;
    private static final int COL_MOVIE_TITLE = 2;


    public HotmovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext=context;
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");

        String mSort = SharedPreferencesUtil.getPreferredLocation(getContext());

        String language = "zh";

        preferenceId = addPreference(mSort);

        createHotMovieApi();

        mHotMovieApi.movieList(mSort, language, BuildConfig.OPEN_WEATHER_MAP_API_KEY).enqueue(mListMovieIfoCallback);

        notifyMovie();

        return ;
    }

    private void createHotMovieApi() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(HotMovieApi.BASE_URL)
                .addConverterFactory(
                        GsonConverterFactory.create()
                );
        Retrofit retrofit = builder.client(httpClient.build()).build();
        mHotMovieApi = retrofit.create(HotMovieApi.class);

        mDetailMovieApi = retrofit.create(DetailMovieApi.class);

    }

    private void notifyMovie(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String displayNotificationsKey = mContext.getString(R.string.pref_enable_notifications_key);
        boolean displayNotifications = prefs.getBoolean(displayNotificationsKey,
                Boolean.parseBoolean(mContext.getString(R.string.pref_enable_notifications_default)));

        if (displayNotifications){
            String lastNotificationKey = mContext.getString(R.string.pref_last_notification);
            String lastSync = prefs.getString(lastNotificationKey, "no_movie");

            Cursor cursor=mContext.getContentResolver().query(HotMovieContract.MovieEntry.CONTENT_URI,
                    MOVIE_COLUMNS,
                    MovieEntry.COLUMN_LOC_KEY + " = ?",
                    new String[]{"1"},
                    null);

            String title="no_movie",overview="";
            if(cursor != null)
            {
                if (cursor.moveToFirst()) {
                    title="New TopMovie :"+cursor.getString(COL_MOVIE_TITLE);
                    overview=cursor.getString(COL_MOVIE_OVERVIEW);
                }
            }

            if (!title.equals(lastSync)) {
                NotificationCompat.Builder builder =
                        new NotificationCompat.Builder(getContext())
                                .setContentTitle(title)
                                .setSmallIcon(R.drawable.dinosaur_40)
                                .setContentText(overview);


                Intent resultIntent=new Intent(mContext, MainActivity.class);

                TaskStackBuilder stackBuilder=TaskStackBuilder.create(mContext);
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent=
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                builder.setContentIntent(resultPendingIntent);

                NotificationManager manager=
                        (NotificationManager)getContext().getSystemService(mContext.NOTIFICATION_SERVICE);
                manager.notify(MOVIE_NOTIFICATION_ID,builder.build());

                SharedPreferences.Editor editor=prefs.edit();
                editor.putString(lastNotificationKey,title);
                editor.commit();
            }
            cursor.close();
        }
    }

    long addPreference(String preference) {
        Cursor preferenceCursor = mContext.getContentResolver().query(
                HotMovieContract.PreferenceEntry.CONTETN_URI,
                new String[]{HotMovieContract.PreferenceEntry._ID},
                HotMovieContract.PreferenceEntry.COLUMN_PREFERECNE_SETTING + " = ?",
                new String[]{preference},
                null);

        if (preferenceCursor.moveToFirst()) {
            int preIndex = preferenceCursor.getColumnIndex(HotMovieContract.PreferenceEntry._ID);
            preferenceId = preferenceCursor.getLong(preIndex);
        } else {
            ContentValues preValues = new ContentValues();

            preValues.put(HotMovieContract.PreferenceEntry.COLUMN_PREFERECNE_SETTING, preference);

            Uri insertedUri = mContext.getContentResolver().insert(
                    HotMovieContract.PreferenceEntry.CONTETN_URI,
                    preValues
            );

            preferenceId = ContentUris.parseId(insertedUri);

        }
        preferenceCursor.close();

        return preferenceId;
    }

    ArrayList<Integer> getAllId() {
        Cursor idCursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                new String[]{MovieEntry.COLUMN_ID},
                null,
                null,
                null
        );
        ArrayList<Integer> integerArrayList = new ArrayList<>();

        while (idCursor.moveToNext()){
            integerArrayList.add(idCursor.getInt(idCursor.getColumnIndex(MovieEntry.COLUMN_ID)));
        }

        Log.i("getALLID", integerArrayList.toString() + " - " + integerArrayList.size());
        idCursor.close();


        return integerArrayList;
    }


    retrofit2.Callback<ListMovieIfo<MovieInfo>> mListMovieIfoCallback = new
            retrofit2.Callback<ListMovieIfo<MovieInfo>>() {
                @Override
                public void onResponse(Call<ListMovieIfo<MovieInfo>> call, Response<ListMovieIfo<MovieInfo>> response) {
                    if (response.isSuccessful()) {

                        ArrayList<ContentValues> arrayList = new ArrayList<>();
                        ListMovieIfo<MovieInfo> listMovieIfo = response.body();

                        for (MovieInfo ifo : listMovieIfo.items) {
                            String poster_path = "http://image.tmdb.org/t/p/w185" + ifo.getPoster_path();
                            String backup_path = "http://image.tmdb.org/t/p/w185" + ifo.getBackdrop_path();
//                                Log.i("FetchMovieTask",poster_path);
                            ContentValues movieValues = new ContentValues();
                            movieValues.put(MovieEntry.COLUMN_LOC_KEY, preferenceId);
                            movieValues.put(MovieEntry.COLUMN_BACKDROP_PATH, backup_path);
                            movieValues.put(MovieEntry.COLUMN_ID, ifo.getId());
                            movieValues.put(MovieEntry.COLUMN_OVERVIEW, ifo.getOverview());
                            movieValues.put(MovieEntry.COLUMN_POSTER_PATH, poster_path);
                            movieValues.put(MovieEntry.COLUMN_TITLE, ifo.getTitle());
                            movieValues.put(MovieEntry.COLUMN_VOTE_AVERAGE, ifo.getVote_average());
                            movieValues.put(MovieEntry.COLUMN_RELEASE_DATE, ifo.getRelease_date());
                            movieValues.put(MovieEntry.COLUMN_VIDEO_SOURCE, "https://www.youtube.com/watch?v=");
                            movieValues.put(MovieEntry.COLUMN_RUN_TIME, "160");

                            arrayList.add(movieValues);
                        }

                        int inserted = 0;
                        if (arrayList.size() > 0) {
                            ContentValues[] cvArray = new ContentValues[arrayList.size()];
                            arrayList.toArray(cvArray);

                            inserted = mContext.getContentResolver().bulkInsert(MovieEntry.CONTENT_URI, cvArray);

                            String append_to_response = "trailers,reviews";

                            //fetch source about review
                            for (int i : getAllId()) {
                                String id = i + "";
                                mDetailMovieApi.getDetailIfo(id, append_to_response, BuildConfig.OPEN_WEATHER_MAP_API_KEY).enqueue(mListMovieDetailIfoCallback);
                            }
                        }
                        Log.d(LOG_TAG, "FetchMovie Complete. " + inserted + " Inserted");
                    } else {
                        Log.d("ListMovieIfoCallback", "Code:" + response.code() +
                                "  Message: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<ListMovieIfo<MovieInfo>> call, Throwable t) {
                    t.printStackTrace();
                }
            };


    retrofit2.Callback<ListMovieDetailIfo> mListMovieDetailIfoCallback = new
            Callback<ListMovieDetailIfo>() {
                @Override
                public void onResponse(Call<ListMovieDetailIfo> call, Response<ListMovieDetailIfo> response) {
                    if (response.isSuccessful()) {
                        ListMovieDetailIfo listDetail = response.body();

                        Log.i("getId",listDetail.getId()+"");

                        ArrayList<ContentValues> vedioList = new ArrayList<>();
                        ArrayList<ContentValues> reviewList = new ArrayList<>();

                        for (Youtube yotube : listDetail.mTrailers.mYoutubeList) {
                            ContentValues youtubeValues = new ContentValues();
                            String path = yotube.getSource();
                            youtubeValues.put(MovieEntry.COLUMN_VIDEO_SOURCE, path);
                            youtubeValues.put(MovieEntry.COLUMN_ID, listDetail.getId());
                            youtubeValues.put(MovieEntry.COLUMN_RUN_TIME, listDetail.getRuntime());
                            vedioList.add(youtubeValues);
                        }

                        for (Review review : listDetail.mReviews.mReviewList) {
                            ContentValues reviewValues = new ContentValues();
                            reviewValues.put(HotMovieContract.ReviewEntry.COLUMN_LOC_KEY, listDetail.getId());
                            reviewValues.put(HotMovieContract.ReviewEntry.COLUMN_AUTHOR, review.getAuthor());
                            reviewValues.put(HotMovieContract.ReviewEntry.COLUMN_CONTENT, review.getContent());
                            reviewList.add(reviewValues);
                            Log.i("getALLID", listDetail.mReviews.mReviewList.size() + "");
                        }

                        if (listDetail.mReviews.mReviewList.size()==0){
                            ContentValues reviewValues = new ContentValues();
                            reviewValues.put(HotMovieContract.ReviewEntry.COLUMN_LOC_KEY, listDetail.getId());
                            reviewValues.put(HotMovieContract.ReviewEntry.COLUMN_AUTHOR, "null");
                            reviewValues.put(HotMovieContract.ReviewEntry.COLUMN_CONTENT, "null");
                            reviewList.add(reviewValues);
                        }

                        if (vedioList.size() > 0) {
                            ContentValues[] cvArray = new ContentValues[vedioList.size()];
                            vedioList.toArray(cvArray);
                            for (ContentValues content : cvArray) {
                                int _id = mContext.getContentResolver().update(MovieEntry.CONTENT_URI, content,
                                        MovieEntry.COLUMN_ID + " = ? ", new String[]{content.getAsString(MovieEntry.COLUMN_ID)});

                                if (_id != 0) {
                                    break;
                                }
                            }
                        }

                        int serted = 0;
                        if (reviewList.size() > 0) {
                            ContentValues[] cvArray = new ContentValues[reviewList.size()];
                            reviewList.toArray(cvArray);
                            serted = mContext.getContentResolver().bulkInsert(HotMovieContract.ReviewEntry.CONTENT_URI, cvArray);
                            Log.d(LOG_TAG, "FetchReview Complete " + serted + " Inserted");
                        }


                    } else {
                        Log.d("ListMovieDetailCallback", "Code:" + response.code() +
                                "  Message: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<ListMovieDetailIfo> call, Throwable t) {
                    t.printStackTrace();
                }
            };


    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {

        int sync_interval=Integer.parseInt(SharedPreferencesUtil.getSynchronization(context));
        int sync_flextime=sync_interval/3;
        /*
         * Since we've created an account
         */
        HotmovieSyncAdapter.configurePeriodicSync(context, sync_interval, sync_flextime);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}