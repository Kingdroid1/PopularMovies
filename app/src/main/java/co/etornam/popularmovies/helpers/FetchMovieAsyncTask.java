package co.etornam.popularmovies.helpers;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import co.etornam.popularmovies.model.Movie;

public class FetchMovieAsyncTask extends AsyncTask<String, Void, Movie[]> {
    private String TAG = FetchMovieAsyncTask.class.getSimpleName();
    private String apiKey;
    private OnTaskCompleted taskCompleted;
    private String sort;

    public FetchMovieAsyncTask(String apiKey, OnTaskCompleted taskCompleted, String sort) {
        this.apiKey = apiKey;
        this.taskCompleted = taskCompleted;
        this.sort = sort;
    }

    @Override
    protected Movie[] doInBackground(String... strings) {
        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;
        String moviesJsonStr = null;
        try {
            URL url = getApiUrl(sort);

            // Start connecting to get JSON
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            InputStream inputStream = httpURLConnection.getInputStream();
            StringBuilder builder = new StringBuilder();

            if (inputStream == null) {
                return null;
            }
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                // Adds '\n' at last line if not already there.
                // This supposedly makes it easier to debug.
                builder.append(line).append("\n");
            }

            if (builder.length() == 0) {
                // No data found. Nothing more to do here.
                return null;
            }

            moviesJsonStr = builder.toString();
        }catch (IOException e) {
            Log.d(TAG, "Error :", e);
            return null;
        }finally {
            // Tidy up: release url connection and buffered reader
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (final IOException e) {
                    Log.d(TAG, "Error closing stream", e);
                }
            }
        }

        try {
            // Make sense of the JSON data
            return getMoviesDataFromJson(moviesJsonStr);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    private URL getApiUrl(String sort) throws MalformedURLException {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(sort)
                .appendQueryParameter("api_key", apiKey);
        Log.d(TAG, "getApiUrl: " + builder.build().toString());
        return new URL(builder.build().toString());
    }

    private Movie[] getMoviesDataFromJson(String moviesJsonStr) throws JSONException {
        final String TAG_RESULTS = "results";
        final String TAG_ORIGINAL_TITLE = "original_title";
        final String TAG_POSTER_PATH = "poster_path";
        final String TAG_OVERVIEW = "overview";
        final String TAG_VOTE_AVERAGE = "vote_average";
        final String TAG_RELEASE_DATE = "release_date";

        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray resultsArray = moviesJson.getJSONArray(TAG_RESULTS);

        Movie[] movies = new Movie[resultsArray.length()];

        for (int i = 0; i < resultsArray.length(); i++) {
            // Initialize each object before it can be used
            movies[i] = new Movie();

            // Object contains all tags we're looking for
            JSONObject movieInfo = resultsArray.getJSONObject(i);

            // Store data in movie object
            movies[i].setOriginalTitle(movieInfo.getString(TAG_ORIGINAL_TITLE));
            movies[i].setPosterPath(movieInfo.getString(TAG_POSTER_PATH));
            movies[i].setOverview(movieInfo.getString(TAG_OVERVIEW));
            movies[i].setVoteAverage(movieInfo.getDouble(TAG_VOTE_AVERAGE));
            movies[i].setReleaseDate(movieInfo.getString(TAG_RELEASE_DATE));
        }

        return movies;
    }

    @Override
    protected void onPostExecute(Movie[] movies) {
        super.onPostExecute(movies);
        taskCompleted.onFetchMoviesTaskCompleted(movies);
    }
}
