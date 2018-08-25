package co.etornam.popularmovies.helpers;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import co.etornam.popularmovies.R;
import co.etornam.popularmovies.model.Movie;

public class ImageAdapter extends BaseAdapter {
    private final Context context;
    private final Movie[] movies;

    public ImageAdapter(Context context, Movie[] movies) {
        this.context = context;
        this.movies = movies;
    }

    @Override
    public int getCount() {
        if (movies == null || movies.length == 0) {
            return -1;
        }

        return movies.length;
    }

    @Override
    public Object getItem(int position) {
        if (movies == null || movies.length == 0) {
            return null;
        }

        return movies[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setAdjustViewBounds(true);
        } else {
            imageView = (ImageView) convertView;
        }

        Picasso.get()
                .load(movies[position].getPosterPath())
                .resize(context.getResources().getInteger(R.integer.tmdb_poster_w185_width),
                        context.getResources().getInteger(R.integer.tmdb_poster_w185_height))
                .error(R.drawable.ic_error)
                .placeholder(R.drawable.ic_loading)
                .into(imageView);

        return imageView;
    }
}
