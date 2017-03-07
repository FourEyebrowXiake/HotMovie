package com.example.fourfish.hotmovie.adapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.fourfish.hotmovie.data.HotMovieContract;

/**
 * Created by fourfish on 2017/3/6.
 */

public abstract class RecyclerViewCursorAdapter<VH extends RecyclerView.ViewHolder>
    extends RecyclerView.Adapter<VH>
{
    private Cursor mCursor;

    public void swapCursor(final Cursor cursor){
        this.mCursor=cursor;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount(){
        return this.mCursor!=null ? this.mCursor.getCount():0;
    }

    public Cursor getItem(final int position){
        if(this.mCursor!=null&&!this.mCursor.isClosed()){
            this.mCursor.moveToPosition(position);
        }
        return this.mCursor;
    }

    public Cursor getCursor(){
        return this.mCursor;
    }

    @Override
    public final void onBindViewHolder(final VH holder,final int position) {
        final Cursor cursor=this.getItem(position);
        Log.i("RecyclerViewCursor:",cursor.getString(cursor.getColumnIndex(HotMovieContract.MovieEntry.COLUMN_POSTER_PATH)));
        this.onBindViewHolder(holder,cursor);

    }

    public abstract void onBindViewHolder(final VH holder,final Cursor cursor);
}
