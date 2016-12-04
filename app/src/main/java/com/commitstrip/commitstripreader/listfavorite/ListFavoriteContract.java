package com.commitstrip.commitstripreader.listfavorite;

import com.commitstrip.commitstripreader.BasePresenter;
import com.commitstrip.commitstripreader.BaseView;

import java.util.List;

/**
 * This specifies the contract between the view and the presenter.
 */
public class ListFavoriteContract {

    interface View extends BaseView<ListFavoriteContract.Presenter> {

        void noResult();

        void showError();

        void updateListFavorite(ListFavoriteDto favorite);
    }

    interface Presenter extends BasePresenter {

        void fetchFavoriteStrip();
    }
}
