package com.example.fourfish.hotmovie;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
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

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fourfish on 2017/2/14.
 */

public class MainFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener{

    private RecyclerView mRecyclerView;
    private List<String> mItems=new ArrayList<>();
    private PosterAdapter mPosterAdapterAdapter;

    private boolean isPrefsChange=false;
    private SharedPreferences.OnSharedPreferenceChangeListener prefListener;

    public MainFragment(){

    }

    @Override
    public void  onStart(){
        super.onStart();

        if((mPosterAdapterAdapter.getItemCount()==0)||(isPrefsChange)) {
            FetchMovieTask movieTask = new FetchMovieTask();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sort = prefs.getString(getString(R.string.pref_units_key),
                    getString(R.string.pref_units_popular));
            movieTask.execute(sort);
            isPrefsChange=false;
        } else {
        // 什么也不做
        }
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
            FetchMovieTask movieTask = new FetchMovieTask();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sort = prefs.getString(getString(R.string.pref_units_key),
                    getString(R.string.pref_units_popular));
            movieTask.execute(sort);
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


    private class PosterHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView mImageView;

        private int mPosition;

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
            mPosition=position;
        }

        @Override
        public void onClick(View view) {
            Intent intent=DetailActivity.newIntent(getActivity(),mPosition);
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

    public class FetchMovieTask extends AsyncTask<String, Void, List<String>>{

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        private List<String> getMovieDataFromJson(String forecastJsonStr)
                throws JSONException {

            List<String> posterPath=new ArrayList<String>();

            // These are the names of the JSON objects that need to be extracted.
            final String MOVIE_LIST = "results";
            final String POSTER_PATH="poster_path";
            final String OVER_VIEW="overview";
            final String RELEASE_DATE="release_date";
            final String ID="id";
            final String TITLE="title";
            final String BACKDROP="backdrop_path";
            final String VOTE_AVERAGE="vote_average";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray movieArray = forecastJson.getJSONArray(MOVIE_LIST);


            for(int i = 0; i < movieArray.length(); i++) {

                String path;

                // Get the JSON object representing the day
                JSONObject dayForecast = movieArray.getJSONObject(i);

                path="http://image.tmdb.org/t/p/w185"+dayForecast.getString(POSTER_PATH);

                Movie movie=new Movie();
                movie.setId(dayForecast.getString(ID));
                movie.setMovieGrade(dayForecast.getString(VOTE_AVERAGE));
                movie.setMovieIntro(dayForecast.getString(OVER_VIEW));
                movie.setMovieTime(dayForecast.getString(RELEASE_DATE));
                movie.setName(dayForecast.getString(TITLE));
                movie.setPictureUrl("http://image.tmdb.org/t/p/w185"+dayForecast.getString(BACKDROP));
                MoviesInfor.newInstance().addMovieInfo(movie);

                posterPath.add(path);

            }
            return posterPath;

        }

        @Override
        protected List<String> doInBackground(String... params) {

            // If there's no zip code, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                return null;
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            String language="zh";


            try {

                final String FORECAST_BASE_URL =
                        "http://api.themoviedb.org/3/movie/"+params[0]+"?";
                final String APPID_PARAM = "api_key";
                final String LANGUAGE="language";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(LANGUAGE,language)
                        .appendQueryParameter(APPID_PARAM, BuildConfig.OPEN_WEATHER_MAP_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieDataFromJson(forecastJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(List<String> result) {
            if (result != null) {
                mItems=result;
                setupAdapter();
            }
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
