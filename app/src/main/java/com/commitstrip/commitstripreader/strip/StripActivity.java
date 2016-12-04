package com.commitstrip.commitstripreader.strip;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.commitstrip.commitstripreader.BaseActivity;
import com.commitstrip.commitstripreader.MyApp;
import com.commitstrip.commitstripreader.R;
import com.commitstrip.commitstripreader.data.source.StripRepositorySingleton;
import com.commitstrip.commitstripreader.util.ActivityUtils;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class StripActivity extends BaseActivity {

    @Inject
    StripPresenter mStripPresenter;

    @NonNull public static String ARGUMENT_STRIP_ID = "ARGUMENT_STRIP_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create the fragment
        StripFragment stripFragment = (StripFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (stripFragment == null) {
            Bundle extras = getIntent().getExtras();

            if (extras != null && extras.containsKey(ARGUMENT_STRIP_ID))
                stripFragment = StripFragment.newInstance(extras.getLong(ARGUMENT_STRIP_ID));
            else
                stripFragment = StripFragment.newInstance();

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), stripFragment, R.id.contentFrame);
        }

        // Create the presenter
        DaggerStripComponent.builder()
                .stripRepositoryComponent(StripRepositorySingleton.getInstance(getApplicationContext()).getStripRepositoryComponent())
                .stripPresenterModule(new StripPresenterModule(stripFragment)).build()
                .inject(this);
    }

}
