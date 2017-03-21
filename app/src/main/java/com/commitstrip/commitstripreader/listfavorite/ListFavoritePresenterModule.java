package com.commitstrip.commitstripreader.listfavorite;

import dagger.Module;
import dagger.Provides;

@Module
public class ListFavoritePresenterModule {

    private final ListFavoriteContract.View mView;

    public ListFavoritePresenterModule(ListFavoriteContract.View view) {
        mView = view;
    }

    @Provides
    ListFavoriteContract.View provideListFavoriteContractView() {
        return mView;
    }

}
