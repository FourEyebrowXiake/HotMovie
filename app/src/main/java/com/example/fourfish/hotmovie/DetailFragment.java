package com.example.fourfish.hotmovie;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by fourfish on 2017/2/15.
 */

public class DetailFragment extends Fragment {

    private ImageView mImageView;
    private TextView mNameText;
    private TextView mDateText;
    private TextView mGradeText;
    private TextView mContentText;

    private Movie moive;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int position=(int)getActivity().getIntent().getSerializableExtra(DetailActivity.EXTRA_MOVIE_ID);
        moive=MoviesInfor.newInstance().getMovie(position);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mImageView=(ImageView)rootView.findViewById(R.id.backdrop);
        mNameText=(TextView)rootView.findViewById(R.id.movie_name);
        mDateText=(TextView)rootView.findViewById(R.id.movie_date);
        mGradeText=(TextView)rootView.findViewById(R.id.movie_grade);
        mContentText=(TextView)rootView.findViewById(R.id.movie_content);

        updateView();

        return rootView;
    }

    public void updateView(){
        Picasso.with(getActivity())
                .load(moive.getPictureUrl())
                .placeholder(R.mipmap.ic_launcher)
                .into(mImageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Toast.makeText(getActivity(),"加载失败",Toast.LENGTH_SHORT);
                    }
                });

        mNameText.setText(moive.getName());
        mContentText.setText(moive.getMovieIntro());
        mDateText.setText("Date:"+moive.getMovieTime());
        mGradeText.setText("Grade:"+moive.getMovieGrade()+"/10");

        Log.i("movie_infor",moive.getString());
    }
}
