package udacity.aly.movies_app;

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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aly on 23/06/16.
 */

public class FetchReviewsTask extends AsyncTask<MovieContent.Movie, Void, List<MovieContent.Review>> {
    private final String LOG_TAG = FetchReviewsTask.class.getSimpleName();
    private ReviewAdapter mReviewAdapter;

    public FetchReviewsTask(ReviewAdapter mReviewAdapter) {
        this.mReviewAdapter = mReviewAdapter;
    }

    @Override
    protected List<MovieContent.Review> doInBackground(MovieContent.Movie... params) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String moviesJsonStr = null;

        try {
            final String REVIEW_BASE_URL = "http://api.themoviedb.org/3/movie/" + params[0].getId() + "/reviews?";
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
