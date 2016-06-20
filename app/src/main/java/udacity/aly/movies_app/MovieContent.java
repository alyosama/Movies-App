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

    public static void addItem(Movie item) {
        MOVIES_ITEMS.add(item);
        MOVIES_ITEM_MAP.put(item.getTitle(), item);
    }

    ;

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
        return new Movie(json_input.getInt(MOVIES_ATTR.id.toString()),
                json_input.getString(MOVIES_ATTR.title.toString()),
                json_input.getString(MOVIES_ATTR.overview.toString()),
                json_input.getString(MOVIES_ATTR.release_date.toString()),
                json_input.getString(MOVIES_ATTR.poster_path.toString()),
                json_input.getDouble(MOVIES_ATTR.vote_average.toString()));
    }

    public static String createPosterUrl(Movie m) {
        return "http://image.tmdb.org/t/p/w185/" + m.getPoster_path();

    }

    public static Review createReviewFromJson(JSONObject jsonObject) throws JSONException {
        return new Review(jsonObject.getString(REVIEW_ATTR.id.toString()),
                jsonObject.getString(REVIEW_ATTR.author.toString()),
                jsonObject.getString(REVIEW_ATTR.content.toString()));
    }


    public static Video createVideoFromJson(JSONObject jsonObject) throws JSONException {
        return new Video(jsonObject.getString(VIDEO_ATTR.id.toString()),
                jsonObject.getString(VIDEO_ATTR.name.toString()),
                jsonObject.getString(VIDEO_ATTR.type.toString()),
                jsonObject.getString(VIDEO_ATTR.key.toString()));
    }

    enum MOVIES_ATTR {id, title, overview, release_date, poster_path, vote_average, favorite}

    enum REVIEW_ATTR {id, author, content}

    enum VIDEO_ATTR {id, name, type, key}

    public static class Video {
        private String id;
        private String name;
        private String type;
        private String key;

        public Video(String id, String name, String type, String key) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.key = key;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public String getKey() {
            return key;
        }
    }

    public static class Review {
        private String id;
        private String author;
        private String content;

        public Review(String id, String author, String content) {
            this.id = id;
            this.author = author;
            this.content = content;
        }

        public String getId() {
            return id;
        }

        public String getAuthor() {
            return author;
        }

        public String getContent() {
            return content;
        }
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
        private int isFavorite;

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

        public int isFavorite() {
            return isFavorite;
        }

        public void setIsFavorite(int isFavorite) {
            this.isFavorite = isFavorite;
        }

        @Override
        public String toString() {
            return getTitle();
        }
    }

}
