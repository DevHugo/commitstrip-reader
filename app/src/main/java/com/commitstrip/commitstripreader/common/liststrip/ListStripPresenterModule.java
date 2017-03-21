package com.commitstrip.commitstripreader.common.liststrip;

import com.commitstrip.commitstripreader.liststrip.ListStripActivity;
import com.commitstrip.commitstripreader.random.RandomStripActivity;

import dagger.Module;
import dagger.Provides;

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * {@link ListStripActivity} or {@link RandomStripActivity}.
 */
@Module
public class ListStripPresenterModule {

    private final ListStripContract.View mView;

    public ListStripPresenterModule(ListStripContract.View view) {
        mView = view;
    }

    @Provides
    ListStripContract.View provideListStripContractView() {
        return mView;
    }

}
