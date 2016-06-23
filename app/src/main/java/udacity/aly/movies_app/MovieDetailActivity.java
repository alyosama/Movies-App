package udacity.aly.movies_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

/**
 * An activity representing a single Movie detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link MovieListActivity}.
 */
public class MovieDetailActivity extends AppCompatActivity {

    MovieDatabaseHelper dbHelper = new MovieDatabaseHelper(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);


        final String arg = getIntent().getStringExtra(MovieDetailFragment.ARG_ITEM_ID);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final MovieContent.Movie m = MovieContent.MOVIES_ITEM_MAP.get(arg);
        if (m.isFavorite() == 1) {
            fab.setActivated(false);
        } else {
            fab.setActivated(true);

        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (m.isFavorite() == 1) {
                    m.setIsFavorite(0);
                    dbHelper.deleteMovie(m);
                    Snackbar.make(view, "UnFavorite", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    view.setActivated(!view.isActivated());

                } else {
                    m.setIsFavorite(1);
                    dbHelper.addMovie(m);
                    Snackbar.make(view, "Favorite", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    view.setActivated(!view.isActivated());

                }
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(MovieDetailFragment.ARG_ITEM_ID, arg);
            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, MovieListActivity.class));
            return true;
        }
        //TODO Create Settings at Menu
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
