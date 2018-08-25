package co.etornam.popularmovies;

import android.app.Application;

import co.etornam.popularmovies.util.FontOverride;

public class PopularMovies extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(new Runnable() {
            @Override
            public void run() {
                FontOverride.setDefaultFont(PopularMovies.this, "DEFAULT", "Comfortaa-Light.ttf");
                FontOverride.setDefaultFont(PopularMovies.this, "MONOSPACE", "Comfortaa-Regular.ttf");
                FontOverride.setDefaultFont(PopularMovies.this, "SERIF", "Comfortaa-Bold.ttf");
                FontOverride.setDefaultFont(PopularMovies.this, "SANS_SERIF", "Comfortaa-Bold.ttf");
            }
        }).start();
    }
}
