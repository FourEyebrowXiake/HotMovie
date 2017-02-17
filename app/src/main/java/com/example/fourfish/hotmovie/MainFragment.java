package com.example.fourfish.hotmovie;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.fourfish.hotmovie.model.MoviesInfor;
import com.example.fourfish.hotmovie.tool.AsyncTaskCompleteListener;
import com.example.fourfish.hotmovie.tool.FetchMovieTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fourfish on 2017/2/14.
 */

public class MainFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener{

    private RecyclerView mRecyclerView;
    private List<String> mItems=new ArrayList<>();
    private PosterAdapter mPosterAdapterAdapter;
    private int mPosition;

    private boolean isPrefsChange=false;
    private SharedPreferences.OnSharedPreferenceChangeListener prefListener;

    public MainFragment(){

    }

    public class FetchMyDataTaskCompleteListener implements AsyncTaskCompleteListener<List<String>>
    {

        @Override
        public void onTaskComplete(List<String> result)
        {
            if (result != null) {
                mItems=result;
                setupAdapter();
            }
        }
    }

    @Override
    public void  onStart(){
        super.onStart();

        if((mPosterAdapterAdapter.getItemCount()==0)||(isPrefsChange)) {
            fetchMovie();
        } else {
        // 什么也不做
        }
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);

        //listener on changed sort order preference:
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                isPrefsChange=true;
            }
        };
        prefs.registerOnSharedPreferenceChangeListener(prefListener);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.mainfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.movie_set ) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }
        if (id==R.id.movie_refresh && isOnline()){
            fetchMovie();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.

        mRecyclerView=(RecyclerView)rootView.findViewById(R.id.recyclerview_movie);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));

        setupAdapter();

        return rootView;
    }

    private void setupAdapter(){
        if(isAdded()){
            mPosterAdapterAdapter=new PosterAdapter(mItems);
            mRecyclerView.setAdapter(mPosterAdapterAdapter);
        }
    }

    /**
     *fetch movie information by AsyncTask
     */
    private void fetchMovie(){
        MoviesInfor.newInstance().clearList();
        FetchMovieTask mFetchMovieTask=new FetchMovieTask(getActivity(),new FetchMyDataTaskCompleteListener());
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort = prefs.getString(getString(R.string.pref_units_key),
                getString(R.string.pref_units_popular));
        mFetchMovieTask.execute(sort);
        isPrefsChange=false;
    }


    private class PosterHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView mImageView;



        public PosterHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mImageView=(ImageView)itemView.findViewById(R.id.fragment_movie_poster_image_view);
        }

        public void bindPoster(String url,int position){
            Picasso.with(getActivity())
                    .load(url)
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
            Log.i("MainFragment:",position+"");

        }

        @Override
        public void onClick(View view) {

            Intent intent=DetailActivity.newIntent(getActivity(),getPosition());
            startActivity(intent);
        }
    }

    private class PosterAdapter extends RecyclerView.Adapter<PosterHolder>{

        private List<String> mPosterUrl;

        public PosterAdapter(List<String> urls){
            mPosterUrl=urls;
        }


        @Override
        public PosterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view=LayoutInflater.from(getActivity()).inflate(R.layout.list_item_fragemnt_main,parent,false);
            return new PosterHolder(view);
        }

        @Override
        public void onBindViewHolder(PosterHolder holder, int position) {
            String url=mPosterUrl.get(position);
            holder.bindPoster(url,position);
        }

        @Override
        public int getItemCount() {
            return mPosterUrl.size();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            isPrefsChange=true;
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

}
