package com.example.fourfish.hotmovie.tool;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.fourfish.hotmovie.data.HotMovieContract;

/**
 * Created by fourfish on 2017/3/18.
 */

public class ChangeCollectTask extends AsyncTask<String, Void, Integer> {
    private final String LOG_TAG = ChangeCollectTask.class.getSimpleName();

    private final Context mContext;

    private String mId;
    private int isCollect;

    public ChangeCollectTask(Context context) {
        mContext = context;
    }

    @Override
    protected Integer doInBackground(String... strings) {
        if (strings.length == 0) {
            return null;
        }

        mId = strings[0];

        Cursor moiveIsCollect=mContext.getContentResolver().query(HotMovieContract.MovieEntry.CONTENT_URI,
                new String[]{HotMovieContract.MovieEntry.COLUMN_COLLECT},
                HotMovieContract.MovieEntry.COLUMN_ID + " = ?",
                new String[]{mId},
                null);

        if (moiveIsCollect.moveToFirst()){
            int preIndex = moiveIsCollect.getColumnIndex(HotMovieContract.MovieEntry.COLUMN_COLLECT);
            isCollect = moiveIsCollect.getInt(preIndex);
        }

        //Check whether it is being collected
        if (isCollect==1){
            isCollect=0;
        }else {
            isCollect=1;
        }

        ContentValues contentValues=new ContentValues();
        contentValues.put(HotMovieContract.MovieEntry.COLUMN_COLLECT,isCollect);
        mContext.getContentResolver().update(HotMovieContract.MovieEntry.CONTENT_URI,contentValues ,
                HotMovieContract.MovieEntry.COLUMN_ID+" = ? ", new String[]{mId});

        return isCollect;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        int is=integer;
        if(is==1){
            Toast.makeText(mContext,"电影已收藏",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(mContext,"电影已从收藏列表中移除",Toast.LENGTH_SHORT).show();
        }

    }
}
