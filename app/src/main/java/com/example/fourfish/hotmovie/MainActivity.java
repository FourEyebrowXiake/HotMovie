package com.example.fourfish.hotmovie;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.fourfish.hotmovie.tool.Utility;

public class MainActivity extends AppCompatActivity {

    private String mPreference;
    private final String MAINFRAGMENT_TAG = "FFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPreference=Utility.getPreferredLocation(this);

        if(savedInstanceState==null){
            getSupportFragmentManager().beginTransaction().add(R.id.container,new MainFragment(),MAINFRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        String preferene= Utility.getPreferredLocation(this);
        if(preferene!=null&&!preferene.equals(mPreference)){
            MainFragment mf=(MainFragment)getSupportFragmentManager().findFragmentByTag(MAINFRAGMENT_TAG);
            if(null!=mf){
                mf.onLocationChanged();
            }
            mPreference=preferene;
        }
    }
}