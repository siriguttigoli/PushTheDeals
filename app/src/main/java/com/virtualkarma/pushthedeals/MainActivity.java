package com.virtualkarma.pushthedeals;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity implements FavoritesFragment
        .OnAddClickedListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    public static final String FAVORITES_AVAILABLE = "favorites_available";
    public static final String ADD_TO_FAV = "add_to_favorites";
    SharedPreferences sharedPreferences;
    boolean favoritesAvaiable;
    boolean addToFav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {

            favoritesAvaiable = areFavoritesAvaialble();
            Log.d(LOG_TAG, "Favorites Available - " + favoritesAvaiable);

            if (favoritesAvaiable) {
                Log.d(LOG_TAG, "Add Favorites Fragment");
                getSupportFragmentManager().beginTransaction().replace(R.id.container,
                        new FavoritesFragment()).commit();

            } else {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, new DealSiteFragment(), "deal_site")
                        .commit();
            }
        }

    }

    private boolean areFavoritesAvaialble() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean fav = preferences.getBoolean(FAVORITES_AVAILABLE, false);
        return fav;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
        //    return true;
        //}

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onAddClicked() {
        switchContent(new DealSiteFragment());
    }

    public void switchContent(Fragment fragment) {
        if (fragment instanceof DealSiteFragment) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment, "deal_site")
                    .addToBackStack("fav")
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();

        }
    }

}
