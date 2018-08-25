package co.etornam.popularmovies.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.etornam.popularmovies.R;
import co.etornam.popularmovies.model.Movie;
import co.etornam.popularmovies.util.DateUtil;

public class MovieDetailsActivity extends AppCompatActivity {

    private static final String TAG = MovieDetailsActivity.class.getSimpleName();
    @BindView(R.id.img_poster)
    ImageView imgPoster;
    @BindView(R.id.tv_rate)
    TextView tvRate;
    @BindView(R.id.tv_release_date)
    TextView tvReleaseDate;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.img_btn_back)
    ImageButton imgBtnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        Movie movie = intent.getParcelableExtra(getString(R.string.parcel_movie));

        tvTitle.setText(movie.getOriginalTitle());
        Picasso.get().load(movie.getPosterPath()).error(R.drawable.ic_error).placeholder(R.drawable.ic_loading).into(imgPoster);
        String overView = movie.getOverview();
        if (overView == null) {
            tvContent.setTypeface(null, Typeface.ITALIC);
            overView = getResources().getString(R.string.no_summary_found);
        }
        tvContent.setText(overView);
        tvRate.setText(movie.getDetailedVoteAverage());

        String releaseDate = movie.getReleaseDate();
        if (releaseDate != null) {
            try {
                releaseDate = DateUtil.getLocalizedDate(this, releaseDate, movie.getDateFormat());
            } catch (ParseException e) {
                Log.e(TAG, "Error with parsing movie release date", e);
            }
        } else {
            tvReleaseDate.setTypeface(null, Typeface.ITALIC);
            releaseDate = getResources().getString(R.string.no_release_date_found);
        }
        tvReleaseDate.setText(releaseDate);
    }

    @OnClick(R.id.img_btn_back)
    public void onViewClicked() {
        Intent intent = new Intent(MovieDetailsActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
