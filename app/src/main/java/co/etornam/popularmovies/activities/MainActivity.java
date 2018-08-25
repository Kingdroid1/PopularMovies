package co.etornam.popularmovies.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.PopupMenu;
import android.widget.Toast;

import co.etornam.popularmovies.R;
import co.etornam.popularmovies.helpers.FetchMovieAsyncTask;
import co.etornam.popularmovies.helpers.ImageAdapter;
import co.etornam.popularmovies.helpers.OnTaskCompleted;
import co.etornam.popularmovies.model.Movie;


public class MainActivity extends AppCompatActivity {
    private GridView gridView;
    private FloatingActionButton fab;
    private String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = findViewById(R.id.fab);
        gridView = findViewById(R.id.gridView);

        if (savedInstanceState == null) {
            getMovieData(getSortMethod());
        } else {
            Parcelable[] parcelable = savedInstanceState.
                    getParcelableArray(getString(R.string.parcel_movie));

            if (parcelable != null) {
                int numMovieObjects = parcelable.length;
                Movie[] movies = new Movie[numMovieObjects];
                for (int i = 0; i < numMovieObjects; i++) {
                    movies[i] = (Movie) parcelable[i];
                }

                gridView.setAdapter(new ImageAdapter(this, movies));
            }
        }
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = (Movie) parent.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), MovieDetailsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra(getResources().getString(R.string.parcel_movie), movie);
                startActivity(intent);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.menu_main, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.action_popular) {
                            updateSharedPrefs(getString(R.string.tmdb_sort_pop_desc));
                            getMovieData(getSortMethod());
                            toolbar.setTitle(getResources().getString(R.string.action_popular));
                        } else if (item.getItemId() == R.id.action_favourite) {
                            Toast.makeText(MainActivity.this, "Favourites", Toast.LENGTH_SHORT).show();
                        } else if (item.getItemId() == R.id.action_rated) {
                            updateSharedPrefs(getString(R.string.tmdb_sort_rate_avg_desc));
                            getMovieData(getSortMethod());
                            toolbar.setTitle(getResources().getString(R.string.action_rated));
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        int numMovieObjects = gridView.getCount();
        if (numMovieObjects > 0) {
            Movie[] movies = new Movie[numMovieObjects];
            for (int i = 0; i < numMovieObjects; i++) {
                movies[i] = (Movie) gridView.getItemAtPosition(i);
            }

            outState.putParcelableArray(getString(R.string.parcel_movie), movies);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_about) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void getMovieData(String sortMethod) {
        if (isNetworkAvailable()) {
            String apiKey = getString(R.string.movie_api_key);

            OnTaskCompleted taskCompleted = new OnTaskCompleted() {
                @Override
                public void onFetchMoviesTaskCompleted(Movie[] movies) {
                    gridView.setAdapter(new ImageAdapter(getApplicationContext(), movies));
                }
            };

            FetchMovieAsyncTask movieTask = new FetchMovieAsyncTask(apiKey, taskCompleted);
            movieTask.execute(sortMethod);
        } else {
            Toast.makeText(this, getString(R.string.error_need_internet), Toast.LENGTH_LONG).show();
        }
    }

    private void updateSharedPrefs(String sortMethod) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.pref_sort_method_key), sortMethod);
        editor.apply();
    }

    private String getSortMethod() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        return prefs.getString(getString(R.string.pref_sort_method_key),
                getString(R.string.tmdb_sort_pop_desc));
    }
}
