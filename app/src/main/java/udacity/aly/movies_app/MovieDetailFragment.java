package udacity.aly.movies_app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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


    }


    @Override
    public void onStart() {
        super.onStart();
        FetchReviewsTask reviewsTask = new FetchReviewsTask();
        reviewsTask.execute();
        FetchVideosTask videosTask = new FetchVideosTask();
        videosTask.execute();
        Log.d("Start Task", "Task Run");
    }

    private void setupReviewRecyclerView(@NonNull RecyclerView reviewsRecyclerView) {
        assert reviewsRecyclerView != null;
        mReviewAdapter = new ReviewAdapter(new ArrayList<MovieContent.Review>());
        reviewsRecyclerView.setAdapter(mReviewAdapter);

    }

    private void setupVideoRecyclerView(@NonNull RecyclerView videosRecyclerView) {
        assert videosRecyclerView != null;
        mVideoAdapter = new VideoAdapter(new ArrayList<MovieContent.Video>());
        videosRecyclerView.setAdapter(mVideoAdapter);
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

            videosRecyclerView = rootView.findViewById(R.id.videos_list);
            setupVideoRecyclerView((RecyclerView) videosRecyclerView);


            reviewsRecyclerView = rootView.findViewById(R.id.reviews_list);
            setupReviewRecyclerView((RecyclerView) reviewsRecyclerView);


        }
        return rootView;
    }

    public class FetchVideosTask extends AsyncTask<String, Void, List<MovieContent.Video>> {
        private final String LOG_TAG = FetchVideosTask.class.getSimpleName();

        @Override
        protected List<MovieContent.Video> doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            try {
                final String REVIEW_BASE_URL = "http://api.themoviedb.org/3/movie/" + mItem.getId() + "/videos?";
                final String API_KEY = "api_key=" + BuildConfig.MOVIE_API_KEY;
                URL url = new URL(REVIEW_BASE_URL + API_KEY);

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
                moviesJsonStr = buffer.toString();
                Log.d(LOG_TAG, moviesJsonStr);

            } catch (IOException e) {
                //Log.e(LOG_TAG, "Error ", e);
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
                return getVideoFromJSON(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }


        private List<MovieContent.Video> getVideoFromJSON(String moviesJsonStr) throws JSONException {
            List<MovieContent.Video> movieList = new ArrayList<>();
            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesListJson = moviesJson.getJSONArray("results");
            for (int i = 0; i < moviesListJson.length(); i++) {
                movieList.add(MovieContent.createVideoFromJson(moviesListJson.getJSONObject(i)));
            }
            return movieList;
        }


        @Override
        protected void onPostExecute(List<MovieContent.Video> videos) {
            if (videos != null) {
                mVideoAdapter.clear();
                for (MovieContent.Video m : videos) {
                    mVideoAdapter.addVideo(m);
                    Log.d("AsyncTask Post", "Video Added " + m.getName());
                }
                mVideoAdapter.notifyDataSetChanged();
            }
        }

    }

    private class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
        private final List<MovieContent.Video> mValues;

        public VideoAdapter(List<MovieContent.Video> items) {
            mValues = items;
        }

        public void addVideo(MovieContent.Video v) {
            mValues.add(v);
        }

        public void clear() {
            mValues.clear();
        }

        @Override
        public VideoAdapter.VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.video_list_content, parent, false);
            return new VideoViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final VideoViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mContentView.setText(mValues.get(position).getName());
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String youtubeUrl = "http://www.youtube.com/watch?v=" + holder.mItem.getKey();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl));
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class VideoViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mContentView;
            public MovieContent.Video mItem;

            public VideoViewHolder(View view) {
                super(view);
                mView = view;
                mContentView = (TextView) view.findViewById(R.id.video_text);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + this.mItem.getName() + "'";
            }
        }
    }


    public class FetchReviewsTask extends AsyncTask<String, Void, List<MovieContent.Review>> {
        private final String LOG_TAG = FetchReviewsTask.class.getSimpleName();

        @Override
        protected List<MovieContent.Review> doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            try {
                final String REVIEW_BASE_URL = "http://api.themoviedb.org/3/movie/" + mItem.getId() + "/reviews?";
                final String API_KEY = "api_key=" + BuildConfig.MOVIE_API_KEY;
                URL url = new URL(REVIEW_BASE_URL + API_KEY);

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
                moviesJsonStr = buffer.toString();
                Log.d(LOG_TAG, moviesJsonStr);

            } catch (IOException e) {
                //Log.e(LOG_TAG, "Error ", e);
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
                return getReviewFromJSON(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }


        private List<MovieContent.Review> getReviewFromJSON(String moviesJsonStr) throws JSONException {
            List<MovieContent.Review> movieList = new ArrayList<>();
            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesListJson = moviesJson.getJSONArray("results");
            for (int i = 0; i < moviesListJson.length(); i++) {
                movieList.add(MovieContent.createReviewFromJson(moviesListJson.getJSONObject(i)));
            }
            return movieList;
        }


        @Override
        protected void onPostExecute(List<MovieContent.Review> reviews) {
            if (reviews != null) {
                mReviewAdapter.clear();
                for (MovieContent.Review m : reviews) {
                    mReviewAdapter.addReview(m);
                    Log.d("AsyncTask Post", "Review Added " + m.getAuthor());
                }
                mReviewAdapter.notifyDataSetChanged();
            }
        }

    }

    private class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
        private final List<MovieContent.Review> mValues;

        public ReviewAdapter(List<MovieContent.Review> items) {
            mValues = items;
        }


        public void addReview(MovieContent.Review r) {
            mValues.add(r);
        }

        public void clear() {
            mValues.clear();
        }

        @Override
        public ReviewAdapter.ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.review_list_content, parent, false);
            return new ReviewViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ReviewViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mAuthorView.setText(mValues.get(position).getAuthor());
            holder.mContentView.setText(mValues.get(position).getContent());

        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ReviewViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mAuthorView;
            public final TextView mContentView;
            public MovieContent.Review mItem;

            public ReviewViewHolder(View view) {
                super(view);
                mView = view;
                mContentView = (TextView) view.findViewById(R.id.review_text);
                mAuthorView = (TextView) view.findViewById(R.id.author_text);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + this.mItem.getId() + "'";
            }
        }
    }
}
