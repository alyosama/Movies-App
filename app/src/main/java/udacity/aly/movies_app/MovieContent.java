package udacity.aly.movies_app;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class MovieContent {
    /**
     * An array of sample (dummy) items.
     */
    public static final List<Movie> MOVIES_ITEMS = new ArrayList<Movie>();
    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, Movie> MOVIES_ITEM_MAP = new HashMap<String, Movie>();
    public static String[] MOVIES_ATTR = {"id", "title", "overview", "release_date", "poster_path", "vote_average"};

    public static void addItem(Movie item) {
        MOVIES_ITEMS.add(item);
        MOVIES_ITEM_MAP.put(item.getTitle(), item);
    }
    public static void clearItems() {
        MOVIES_ITEMS.clear();
        MOVIES_ITEM_MAP.clear();
    }


    /**
     * Movie Factory From Json Input
     *
     * @param json_input
     * @return
     * @throws JSONException
     */
    public static Movie createMovieFromJson(JSONObject json_input) throws JSONException {
        return new Movie(json_input.getInt(MOVIES_ATTR[0]), json_input.getString(MOVIES_ATTR[1]),
                json_input.getString(MOVIES_ATTR[2]), json_input.getString(MOVIES_ATTR[3]),
                json_input.getString(MOVIES_ATTR[4]), json_input.getDouble(MOVIES_ATTR[5]));
    }

    public static String createPosterUrl(Movie m) {
        return "http://image.tmdb.org/t/p/w185/" + m.getPoster_path();

    }
    /**
     * A dummy item representing a piece of content.
     */
    public static class Movie {

        private int id;
        private String title;
        private String overview;
        private String release_date;
        private String poster_path;
        private double vote_average;

        public Movie(int id, String title, String overview, String release_date, String poster_path, double vote_average) {
            this.id = id;
            this.title = title;
            this.overview = overview;
            this.release_date = release_date;
            this.poster_path = poster_path;
            this.vote_average = vote_average;
        }


        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getOverview() {
            return overview;
        }

        public String getRelease_date() {
            return release_date;
        }

        public String getPoster_path() {
            return poster_path;
        }

        public double getVote_average() {
            return vote_average;
        }

        @Override
        public String toString() {
            return getTitle();
        }
    }

}
