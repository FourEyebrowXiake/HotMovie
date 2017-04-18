package com.example.fourfish.hotmovie;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.fourfish.hotmovie.tool.SharedPreferencesUtil;

public class MainActivity extends AppCompatActivity implements MainFragment.Callback {

    private String mPreference;
    private boolean mTwoPane;

    private final String MAINFRAGMENT_TAG = "FFTAG";
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPreference= SharedPreferencesUtil.getPreferredLocation(this);

        if(findViewById(R.id.container)!=null){
            mTwoPane=true;
            if(savedInstanceState==null){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container,new DetailFragment(),DETAILFRAGMENT_TAG)
                        .commit();
            }

            Log.i("MainActivity","TWO");
        }else {
            mTwoPane=false;
            Log.i("MainActivity","ONE");
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        String preferene= SharedPreferencesUtil.getPreferredLocation(this);
        if(preferene!=null&&!preferene.equals(mPreference)){
            MainFragment mf=(MainFragment)getSupportFragmentManager().findFragmentByTag(MAINFRAGMENT_TAG);
            if(null!=mf){
                mf.onPreferenceChanged();
            }
            DetailFragment df = (DetailFragment)getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if ( null != df ) {
                df.onPreferenceChanged(preferene);
            }
            mPreference=preferene;
        }
    }

    @Override
    public void onItemSelected(Uri dateUri) {
        if(mTwoPane){
            Bundle args=new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI,dateUri);

            DetailFragment fragment=new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container,fragment,DETAILFRAGMENT_TAG)
                    .commit();
        }else {
            Intent intent=new Intent(this,DetailActivity.class)
                    .setData(dateUri);
            startActivity(intent);

        }
    }
}