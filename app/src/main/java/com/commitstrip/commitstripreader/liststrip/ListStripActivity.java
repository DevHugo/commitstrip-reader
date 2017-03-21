package com.commitstrip.commitstripreader.liststrip;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.commitstrip.commitstripreader.BaseActivity;
import com.commitstrip.commitstripreader.MyApp;
import com.commitstrip.commitstripreader.R;
import com.commitstrip.commitstripreader.common.liststrip.ListStripFragment;
import com.commitstrip.commitstripreader.common.liststrip.ListStripPresenterModule;
import com.commitstrip.commitstripreader.intro.IntroActivity;
import com.commitstrip.commitstripreader.util.ActivityUtils;

import javax.inject.Inject;

public class ListStripActivity extends BaseActivity {

    @Inject
    ListStripPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(getString(R.string.liststrip));

        // Get user preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // If it our first run, we schedule a job for synchronize the database.
        if (sharedPreferences.getBoolean("firstrun", true)) {

            Intent intent = new Intent(getBaseContext(), IntroActivity.class);
            startActivity(intent);

            // Don't forget, we can not allow the user to hit return before we setup the service
            finish();
        } else {
            // Create the fragment
            ListStripFragment listStripFragment =
                    (ListStripFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

            if (listStripFragment == null) {
                listStripFragment = ListStripFragment.newInstance();

                ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), listStripFragment, R.id.contentFrame);
            }

            // Create the presenter
            DaggerListStripComponent.builder()
                    .dataSourceComponent(((MyApp) getApplication()).getDataSourceComponent())
                    .listStripPresenterModule(new ListStripPresenterModule(listStripFragment)).build()
                    .inject(this);
        }


    }

}
