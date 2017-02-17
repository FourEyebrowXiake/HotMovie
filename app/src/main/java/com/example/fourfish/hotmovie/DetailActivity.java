package com.example.fourfish.hotmovie;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by fourfish on 2017/2/15.
 */

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE="movie_detail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if(savedInstanceState==null){
            getSupportFragmentManager().beginTransaction().add(R.id.container,new DetailFragment())
                    .commit();
        }
    }

    public static Intent newIntent(Context context, int position){
        Intent intent=new Intent(context,DetailActivity.class);
        intent.putExtra(EXTRA_MOVIE,position);
        return intent;
    }


}