package com.commitstrip.commitstripreader.random;

import android.os.Bundle;

import com.commitstrip.commitstripreader.BaseActivity;
import com.commitstrip.commitstripreader.MyApp;
import com.commitstrip.commitstripreader.R;
import com.commitstrip.commitstripreader.common.liststrip.ListStripFragment;
import com.commitstrip.commitstripreader.common.liststrip.ListStripPresenterModule;
import com.commitstrip.commitstripreader.util.ActivityUtils;

import javax.inject.Inject;

public class RandomStripActivity extends BaseActivity {

    @Inject
    public RandomStripPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(getString(R.string.random));

        // Create the fragment
        ListStripFragment listStripFragment =
                (ListStripFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (listStripFragment == null) {
            listStripFragment = ListStripFragment.newInstance();

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), listStripFragment,
                    R.id.contentFrame);
        }

        // Create the presenter

        DaggerRandomStripComponent.builder()
                .dataSourceComponent(((MyApp) getApplication()).getDataSourceComponent())
                .listStripPresenterModule(new ListStripPresenterModule(listStripFragment)).build()
                .inject(this);

    }

}
