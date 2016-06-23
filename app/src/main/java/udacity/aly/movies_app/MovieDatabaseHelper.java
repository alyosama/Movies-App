package udacity.aly.movies_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aly on 19/06/16.
 */
public class MovieDatabaseHelper extends SQLiteOpenHelper {

    public static final String MOVIE_DATABASE_NAME = "movie.db";
    public static final int MOVIE_DATABASE_VERSION = 3;
    public static final String MOVIES_TABLE_NAME = "Movies";


    public MovieDatabaseHelper(Context context) {
        super(context, MOVIE_DATABASE_NAME, null, MOVIE_DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlStr = "Create Table " + MOVIES_TABLE_NAME + "(" +
                " " + MovieContent.MOVIES_ATTR.id.toString() + " int primary key" +
                "," + MovieContent.MOVIES_ATTR.title.toString() + " text not null" +
                "," + MovieContent.MOVIES_ATTR.overview.toString() + " text not null" +
                "," + MovieContent.MOVIES_ATTR.release_date.toString() + " text not null" +
                "," + MovieContent.MOVIES_ATTR.poster_path.toString() + " text not null" +
                "," + MovieContent.MOVIES_ATTR.vote_average.toString() + " real not null" +
                "," + MovieContent.MOVIES_ATTR.favorite.toString() + " int default 0" + ")";
        db.execSQL(sqlStr);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("Drop table " + MOVIES_TABLE_NAME);
        onCreate(db);
    }


    public void addMovie(MovieContent.Movie movie) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MovieContent.MOVIES_ATTR.id.toString(), movie.getId());
        values.put(MovieContent.MOVIES_ATTR.title.toString(), movie.getTitle());
        values.put(MovieContent.MOVIES_ATTR.overview.toString(), movie.getOverview());
        values.put(MovieContent.MOVIES_ATTR.release_date.toString(), movie.getRelease_date());
        values.put(MovieContent.MOVIES_ATTR.poster_path.toString(), movie.getPoster_path());
        values.put(MovieContent.MOVIES_ATTR.vote_average.toString(), movie.getVote_average());
        values.put(MovieContent.MOVIES_ATTR.favorite.toString(), movie.isFavorite());
        db.insertWithOnConflict(MOVIES_TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        Log.d("Movie Database", movie.getTitle() + " Added at Favorite");

    }

    public void deleteMovie(MovieContent.Movie movie) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(MOVIES_TABLE_NAME, MovieContent.MOVIES_ATTR.id.toString() + "=?", new String[]{String.valueOf(movie.getId())});
        Log.d("Movie Database", movie.getTitle() + " Removed From Favorite");
    }
    public List<MovieContent.Movie> getMovies() {
        List<MovieContent.Movie> movies = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + MOVIES_TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                MovieContent.Movie movie = new MovieContent.Movie(
                        cursor.getInt(cursor.getColumnIndex(MovieContent.MOVIES_ATTR.id.toString())),
                        cursor.getString(cursor.getColumnIndex(MovieContent.MOVIES_ATTR.title.toString())),
                        cursor.getString(cursor.getColumnIndex(MovieContent.MOVIES_ATTR.overview.toString())),
                        cursor.getString(cursor.getColumnIndex(MovieContent.MOVIES_ATTR.release_date.toString())),
                        cursor.getString(cursor.getColumnIndex(MovieContent.MOVIES_ATTR.poster_path.toString())),
                        cursor.getDouble(cursor.getColumnIndex(MovieContent.MOVIES_ATTR.vote_average.toString()))
                );
                movie.setIsFavorite(cursor.getInt(cursor.getColumnIndex(MovieContent.MOVIES_ATTR.favorite.toString())));
                movies.add(movie);
                Log.d("Movie Database", movie.getTitle() + " Get from Database");
            } while (cursor.moveToNext());
        }
        cursor.close();
        return movies;
    }
}
