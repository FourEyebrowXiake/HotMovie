package com.example.fourfish.hotmovie.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.fourfish.hotmovie.R;

import java.util.List;

/**
 * Created by fourfish on 2017/3/8.
 */

public class ReviewAdapter extends ArrayAdapter<com.example.fourfish.hotmovie.Entry.Review> {

    private int resourceId;

    public ReviewAdapter(Context context, int resource, List<com.example.fourfish.hotmovie.Entry.Review> objects) {
        super(context, resource, objects);
        resourceId=resource;
    }

    @Override
    public View getView(int position, View convert, ViewGroup parent){
        com.example.fourfish.hotmovie.Entry.Review review=getItem(position);
        View view= LayoutInflater.from(getContext()).inflate(resourceId,null);
        TextView author=(TextView) view.findViewById(R.id.review_author);
        TextView content=(TextView) view.findViewById(R.id.review_content);
        author.setText(review.author);
        content.setText(review.content);
        return view;
    }
}
