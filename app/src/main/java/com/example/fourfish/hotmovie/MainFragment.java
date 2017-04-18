package com.example.fourfish.hotmovie;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.fourfish.hotmovie.adapter.MyCursorAdapter;
import com.example.fourfish.hotmovie.adapter.OnItemClickListener;
import com.example.fourfish.hotmovie.data.HotMovieContract;
import com.example.fourfish.hotmovie.tool.FetchMovieTask;
import com.example.fourfish.hotmovie.tool.SharedPreferencesUtil;

/**
 * Created by fourfish on 2017/2/14.
 */

public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int LOADER_SEARCH_RESULTS=0;

    private RecyclerView mRecyclerView;
    private MyCursorAdapter mPosterAdapter;

    private boolean isPrefsChange=false;
    private boolean isCollect=false;
    private int mPosition=0;

    public class onItemClickLister implements OnItemClickListener{

        @Override
        public void OnItemClicked(Cursor cursor,int position) {
            if (cursor!=null){
                ((Callback)getActivity()).onItemSelected(HotMovieContract.ReviewEntry
                        .buildReviewWithMovie(cursor.getLong(cursor.getColumnIndex(HotMovieContract.MovieEntry.COLUMN_ID))));
            }

            mPosition=position;
        }
    }

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri dateUri);
    }

    @Override
    public void  onStart(){
        super.onStart();
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_SEARCH_RESULTS, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
        Log.i("MainFragment","ARRIVE");
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
        if(id==R.id.movie_collect){
            if (isCollect){
                isCollect=false;
            }else {
                isCollect=true;
            }
            getLoaderManager().restartLoader(LOADER_SEARCH_RESULTS,null,this);
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

        StaggeredGridLayoutManager staggeredGridLayoutManager=new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);

        mPosterAdapter=new MyCursorAdapter(getActivity());
        mPosterAdapter.setOnItemClickListener(new onItemClickLister());

        mRecyclerView.setAdapter(mPosterAdapter);

        return rootView;
    }


    /**
     *fetch movie information by AsyncTask
     */
    private void fetchMovie(){
        FetchMovieTask mFetchMovieTask=new FetchMovieTask(getActivity());
        String sort = SharedPreferencesUtil.getPreferredLocation(getActivity());
        mFetchMovieTask.execute(sort);

        isPrefsChange=false;
    }


    void onPreferenceChanged( ) {
        fetchMovie();
        getLoaderManager().restartLoader(LOADER_SEARCH_RESULTS, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String preference= SharedPreferencesUtil.getPreferredLocation(getActivity());

        //fetch all movie or trove
        Uri uri;

        if (!isCollect) {
            uri = HotMovieContract.MovieEntry.buildMoviePreference(preference);
        }else {
            uri=HotMovieContract.MovieEntry.buildMoviePreferenceIfCollect(preference,1);
        }

        return new CursorLoader(getActivity(),
                uri,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        this.mPosterAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        this.mPosterAdapter.swapCursor(null);
    }


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

}
