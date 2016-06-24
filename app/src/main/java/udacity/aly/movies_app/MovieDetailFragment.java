package udacity.aly.movies_app;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link MovieListActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "movie_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private MovieContent.Movie mItem;
    private View reviewsRecyclerView;
    private View videosRecyclerView;
    private VideoAdapter mVideoAdapter;
    private ReviewAdapter mReviewAdapter;
    private MovieDatabaseHelper dbHelper;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItem = MovieContent.MOVIES_ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.getTitle());
            }


        }


        dbHelper = new MovieDatabaseHelper(getContext());
    }


    @Override
    public void onStart() {
        super.onStart();
        FetchReviewsTask reviewsTask = new FetchReviewsTask(mReviewAdapter);
        reviewsTask.execute(mItem);
        FetchVideosTask videosTask = new FetchVideosTask(mVideoAdapter);
        videosTask.execute(mItem);
        Log.d("Start Task", "Task Run");
    }

    private void setupReviewRecyclerView(@NonNull RecyclerView reviewsRecyclerView) {
        assert reviewsRecyclerView != null;
        mReviewAdapter = new ReviewAdapter(new ArrayList<MovieContent.Review>());
        reviewsRecyclerView.setAdapter(mReviewAdapter);

    }

    private void setupVideoRecyclerView(@NonNull RecyclerView videosRecyclerView) {
        assert videosRecyclerView != null;
        mVideoAdapter = new VideoAdapter(getContext(), new ArrayList<MovieContent.Video>());
        videosRecyclerView.setAdapter(mVideoAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_detail, container, false);

        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.movie_title)).setText(mItem.getTitle());
            ((TextView) rootView.findViewById(R.id.movie_detail)).setText(mItem.getOverview());
            ((TextView) rootView.findViewById(R.id.movie_ratingView)).setText(String.valueOf(mItem.getVote_average()) + "/10");
            ((TextView) rootView.findViewById(R.id.release_dateView)).setText(mItem.getRelease_date());
            ImageView posterImage = ((ImageView) rootView.findViewById(R.id.poster_imageView));
            Picasso.with(getActivity()).load(MovieContent.createPosterUrl(mItem))
                    .placeholder(R.drawable.ic_camera_roll).fit()
                    .into(posterImage);

            videosRecyclerView = rootView.findViewById(R.id.videos_list);
            setupVideoRecyclerView((RecyclerView) videosRecyclerView);


            reviewsRecyclerView = rootView.findViewById(R.id.reviews_list);
            setupReviewRecyclerView((RecyclerView) reviewsRecyclerView);

            final FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);

            if (mItem.isFavorite() == 1) {
                fab.setImageResource(R.drawable.ic_favorite);
            } else {
                fab.setImageResource(R.drawable.ic_grade);

            }
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mItem.isFavorite() == 1) {
                        mItem.setIsFavorite(0);
                        dbHelper.deleteMovie(mItem);
                        Snackbar.make(view, "UnFavorite", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        fab.setImageResource(R.drawable.ic_grade);

                    } else {
                        mItem.setIsFavorite(1);
                        dbHelper.addMovie(mItem);
                        Snackbar.make(view, "Favorite", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        fab.setImageResource(R.drawable.ic_favorite);
                    }
                }
            });

        }
        return rootView;
    }

}
