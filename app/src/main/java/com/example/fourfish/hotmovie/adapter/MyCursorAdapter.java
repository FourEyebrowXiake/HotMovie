package com.example.fourfish.hotmovie.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.fourfish.hotmovie.R;
import com.example.fourfish.hotmovie.data.HotMovieContract;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by fourfish on 2017/3/6.
 */

public class MyCursorAdapter extends RecyclerViewCursorAdapter<MyCursorAdapter.MyViewHolder>
    implements View.OnClickListener
{
    private final LayoutInflater mLayoutInflater;
    private OnItemClickListener mOnItemClickListener;
    private Context mContext;

    public MyCursorAdapter(final Context context){
        super();

        this.mContext=context;
        this.mLayoutInflater=LayoutInflater.from(context);
    }

    public void setOnItemClickListener(final OnItemClickListener onItemClickListener){
        this.mOnItemClickListener=onItemClickListener;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, Cursor cursor) {
//        Log.i("MyCursorAdapter:",cursor.getString(cursor.getColumnIndex(HotMovieContract.MovieEntry.COLUMN_POSTER_PATH)));
            holder.bindData(cursor,mContext);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view=this.mLayoutInflater.inflate(R.layout.list_item_fragemnt_main,parent,false);
        view.setOnClickListener(this);
        return new MyViewHolder(view);
    }

    @Override
    public void onClick(View view) {
        if(this.mOnItemClickListener!=null){
            final RecyclerView recyclerView=(RecyclerView)view.getParent();
            final int position=recyclerView.getChildLayoutPosition(view);
            if(position!=RecyclerView.NO_POSITION){
                final Cursor cursor=this.getItem(position);
                this.mOnItemClickListener.OnItemClicked(cursor);
            }
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.fragment_movie_poster_image_view)
        ImageView mImageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        public void bindData(final Cursor cursor , final Context context){
//            Log.i("MyCursorAdapter:",cursor.getString(cursor.getColumnIndex(HotMovieContract.MovieEntry.COLUMN_POSTER_PATH)));
            Picasso.with(context)
                    .load(cursor.getString(cursor.getColumnIndex(HotMovieContract.MovieEntry.COLUMN_POSTER_PATH)))
                    .placeholder(R.mipmap.ic_launcher)
                    .into(mImageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Toast.makeText(context,"加载失败",Toast.LENGTH_SHORT);
                        }
                    });
        }
    }

}
