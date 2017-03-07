package com.example.fourfish.hotmovie;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fourfish.hotmovie.model.Movie;
import com.example.fourfish.hotmovie.model.MoviesInfor;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by fourfish on 2017/2/15.
 */

public class DetailFragment extends Fragment {

     @BindView(R.id.image_backdrop)ImageView mImageView;

     @BindView(R.id.movie_date) TextView mDateText;
     @BindView(R.id.movie_grade) TextView mGradeText;
     @BindView(R.id.movie_content) TextView mContentText;
     @BindView(R.id.toolbar) Toolbar mToolbar;
     @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbarLayout;

    private Movie moive;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle=getActivity().getIntent().getExtras();
        int p=0;
        if(bundle!=null) {
            p=bundle.getInt(DetailActivity.EXTRA_MOVIE);
        }
        moive= MoviesInfor.newInstance().getMovie(p);
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        ButterKnife.bind(this,rootView);

        updateView();

        return rootView;
    }

    public void updateView(){

        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mCollapsingToolbarLayout.setTitle(moive.getName());
        //mCollapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

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

        mContentText.setText(moive.getMovieIntro());
        mDateText.setText("Date:"+moive.getMovieTime());
        mGradeText.setText("Grade:"+moive.getMovieGrade()+"/10");

        Log.i("movie_infor",moive.getString());
    }
}
