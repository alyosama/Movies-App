package udacity.aly.movies_app;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = MovieContent.MOVIES_ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.getTitle());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_detail, container, false);

        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.movie_detail)).setText(mItem.getOverview());
            ((TextView) rootView.findViewById(R.id.movie_ratingView)).setText(String.valueOf(mItem.getVote_average()) + "/10");
            ((TextView) rootView.findViewById(R.id.release_dateView)).setText(mItem.getRelease_date());
            ImageView posterImage = ((ImageView) rootView.findViewById(R.id.poster_imageView));
            Picasso.with(getActivity()).load(MovieContent.createPosterUrl(mItem))
                    .placeholder(R.drawable.ic_camera_roll).fit()
                    .into(posterImage);
        }
        //movie poster image thumbnail
        //A plot synopsis (called overview in the api)
        //user rating (called vote_average in the api)
        //release date


        return rootView;
    }
}
