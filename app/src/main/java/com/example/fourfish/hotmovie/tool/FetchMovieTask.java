package com.example.fourfish.hotmovie.tool;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.fourfish.hotmovie.BuildConfig;
import com.example.fourfish.hotmovie.model.Movie;
import com.example.fourfish.hotmovie.model.MoviesInfor;

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
 * Created by fourfish on 2017/2/16.
 */

public class FetchMovieTask extends AsyncTask<String, Void, List<String>> {

    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();


    private Context context;
    private AsyncTaskCompleteListener<List<String>> listener;

    public FetchMovieTask(Context ctx, AsyncTaskCompleteListener<List<String>> listener)
    {
        this.context = ctx;
        this.listener = listener;
    }


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
        super.onPostExecute(result);
        listener.onTaskComplete(result);
    }
}
