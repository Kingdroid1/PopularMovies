package co.etornam.popularmovies.helpers;

import co.etornam.popularmovies.model.Movie;

public interface OnTaskCompleted {
    void onFetchMoviesTaskCompleted(Movie[] movies);
}
