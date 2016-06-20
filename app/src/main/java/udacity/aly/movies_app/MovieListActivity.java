package udacity.aly.movies_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
 * An activity representing a list of Movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MovieDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MovieListActivity extends AppCompatActivity {

    MovieDatabaseHelper dbHelper = new MovieDatabaseHelper(this);
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private MovieAdapter mMovieAdapter;
    private View recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        recyclerView = findViewById(R.id.movie_list);
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        fetchMovies();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        if(id ==R.id.action_refresh){
            fetchMovies();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void fetchMovies(){
        FetchMoviesTask moviesTask = new FetchMoviesTask();
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String sort_type=prefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_default));
        moviesTask.execute(sort_type);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        assert recyclerView != null;
        mMovieAdapter=new MovieAdapter(MovieContent.MOVIES_ITEMS);
        //int colCount=3;
        //recyclerView.setLayoutManager(new GridLayoutManager(this,colCount));
        recyclerView.setAdapter(mMovieAdapter);
    }


    public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {
        private final List<MovieContent.Movie> mValues;
        public MovieAdapter(List<MovieContent.Movie> items) {
            mValues = items;
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.movie_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            Picasso.with(getApplicationContext()).load(MovieContent.createPosterUrl(mValues.get(position)))
                    .placeholder(R.drawable.ic_camera_roll).into(holder.mContentView);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(MovieDetailFragment.ARG_ITEM_ID, holder.mItem.getTitle());
                        MovieDetailFragment fragment = new MovieDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.movie_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, MovieDetailActivity.class);
                        intent.putExtra(MovieDetailFragment.ARG_ITEM_ID, holder.mItem.getTitle());

                        context.startActivity(intent);
                    }
                }
            });
        }
        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final ImageView mContentView;
            public MovieContent.Movie mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mContentView = (ImageView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mItem.getTitle() + "'";
            }
        }
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, List<MovieContent.Movie>> {
        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected List<MovieContent.Movie> doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }
            if (params[0].equals(getString(R.string.pref_sort_favorite))) {
                return fetchMoviesFromDatabase();
            } else {
                return fetchMoviesFromWeb(params[0]);
            }
        }

        protected List<MovieContent.Movie> fetchMoviesFromDatabase() {
            return dbHelper.getMovies();
        }

        protected List<MovieContent.Movie> fetchMoviesFromWeb(String param) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            try {
                final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/";
                final String movies_settings = param + "?";
                final String API_KEY = "api_key=" + BuildConfig.MOVIE_API_KEY;
                URL url = new URL(MOVIE_BASE_URL + movies_settings + API_KEY);

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
                return getMoviesFromJSON(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }


        private List<MovieContent.Movie> getMoviesFromJSON(String moviesJsonStr) throws JSONException {
            List<MovieContent.Movie> movieList = new ArrayList<>();
            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesListJson = moviesJson.getJSONArray("results");
            for (int i = 0; i < moviesListJson.length(); i++) {
                movieList.add(MovieContent.createMovieFromJson(moviesListJson.getJSONObject(i)));
            }
            return movieList;
        }


        @Override
        protected void onPostExecute(List<MovieContent.Movie> movies) {
            if (movies != null) {
                MovieContent.clearItems();
                for (MovieContent.Movie m : movies) {
                    MovieContent.addItem(m);
                    Log.d("AsyncTask Post","Movie Added "+m.getTitle());
                }
                mMovieAdapter.notifyDataSetChanged();
            }
        }

    }

}
