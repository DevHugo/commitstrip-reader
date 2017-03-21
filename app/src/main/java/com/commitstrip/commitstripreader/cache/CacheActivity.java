package com.commitstrip.commitstripreader.cache;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.commitstrip.commitstripreader.BaseActivity;
import com.commitstrip.commitstripreader.MyApp;
import com.commitstrip.commitstripreader.R;
import com.commitstrip.commitstripreader.common.displaystrip.DisplayStripFragment;
import com.commitstrip.commitstripreader.util.ActivityUtils;

import javax.inject.Inject;

public class CacheActivity extends BaseActivity {

    @Inject CachePresenter mCachePresenter;

    public static Bundle newInstance() { return new Bundle(); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create the fragment
        CacheFragment cacheFragment =
                (CacheFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (cacheFragment == null) {

            cacheFragment = CacheFragment.newInstance();

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), cacheFragment,
                    R.id.contentFrame);
        }
        // Inject the presenter with Dagger2
        DaggerCacheComponent.builder()
                .dataSourceComponent(((MyApp) getApplication()).getDataSourceComponent())
                .cachePresenterModule(new CachePresenterModule(cacheFragment)).build()
                .inject(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mCachePresenter = null;
    }
}
