package com.commitstrip.commitstripreader;

import static com.commitstrip.commitstripreader.configuration.Configuration.OFFLINE_MODE;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Switch;

import com.commitstrip.commitstripreader.cache.CacheActivity;
import com.commitstrip.commitstripreader.fromthebeginning.FromTheBeginningActivity;
import com.commitstrip.commitstripreader.listfavorite.ListFavoriteActivity;
import com.commitstrip.commitstripreader.liststrip.ListStripActivity;
import com.commitstrip.commitstripreader.random.RandomStripActivity;
import com.commitstrip.commitstripreader.settings.SettingsActivity;
import com.commitstrip.commitstripreader.util.CheckInternetConnection;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    private String TAG = "BaseActivity";

    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view) NavigationView mNavigationView;
    @BindView(R.id.toolbar) Toolbar mMainToolbar;

    private ActionBarDrawerToggle mDrawerToggle;
    private Snackbar snackBarOfflineMode;

    protected boolean isInternetAvailable = CheckInternetConnection.isOnline();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_strip);

        // Ask ButterKnife to inject view field
        ButterKnife.bind(this);

        // Set a Toolbar to replace the ActionBar.
        setSupportActionBar(mMainToolbar);

        // Setup drawer view
        setupDrawerContent(mNavigationView);

        // Find our drawer view
        mDrawerToggle = setupDrawerToggle();

        // Tie DrawerLayout events to the ActionBarToggle
        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {

        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    selectDrawerItem(menuItem);
                    return true;
                });

        MenuItem menuItem = navigationView.getMenu().findItem(R.id.offline_wrapper);
        Switch aSwitch = (Switch) menuItem.getActionView().findViewById(R.id.offline);
        aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked || (!isChecked && isInternetAvailable)) {
                OFFLINE_MODE = isChecked;

                aSwitch.setChecked(OFFLINE_MODE);

                if (OFFLINE_MODE) {

                    snackBarOfflineMode = Snackbar.make(findViewById(R.id.drawer_layout), getString(R.string.activity_base_explain_offline), Snackbar.LENGTH_INDEFINITE)
                            .setActionTextColor(Color.RED);
                    snackBarOfflineMode.show();

                    aSwitch.setChecked(OFFLINE_MODE);
                } else {

                    if (snackBarOfflineMode != null) {
                        snackBarOfflineMode.dismiss();
                    }
                }
            } else {
                aSwitch.setChecked(!isChecked);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        MenuItem menuItem = mNavigationView.getMenu().findItem(R.id.offline_wrapper);
        Switch aSwitch = (Switch) menuItem.getActionView().findViewById(R.id.offline);

        if (!isInternetAvailable) {
            OFFLINE_MODE = true;
            aSwitch.setChecked(OFFLINE_MODE);
        } else {
            aSwitch.setChecked(OFFLINE_MODE);
        }

        if (OFFLINE_MODE) {
            snackBarOfflineMode.show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mMainToolbar.setNavigationOnClickListener(null);
        mMainToolbar.setOnMenuItemClickListener(null);
    }

    public void selectDrawerItem(MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.fav:
                Intent intentFav = new Intent(getApplicationContext(), ListFavoriteActivity.class);
                startActivity(intentFav);
                break;
            case R.id.last:
                Intent intentLast = new Intent(getApplicationContext(), ListStripActivity.class);
                startActivity(intentLast);
                break;
            case R.id.settings:
                Intent intentSettings = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intentSettings);
                break;
            case R.id.random:
                Intent intentRandom = new Intent(getApplicationContext(),
                        RandomStripActivity.class);
                startActivity(intentRandom);
                break;
            case R.id.readFromTheBeginning:
                Intent intentFromTheBeginning = new Intent(getApplicationContext(),
                        FromTheBeginningActivity.class);
                startActivity(intentFromTheBeginning);
                break;
            case R.id.cache:
                Intent intentCache = new Intent(getApplicationContext(), CacheActivity.class);
                startActivity(intentCache);
                break;
        }

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);

        // Close the navigation drawer
        mDrawerLayout.closeDrawers();
    }

    private ActionBarDrawerToggle setupDrawerToggle() {

        return new ActionBarDrawerToggle(this, mDrawerLayout, mMainToolbar, R.string.drawer_open,
                R.string.drawer_close);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

}
