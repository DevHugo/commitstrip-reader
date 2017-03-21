package com.commitstrip.commitstripreader.listfavorite;

import com.commitstrip.commitstripreader.BasePresenter;
import com.commitstrip.commitstripreader.BaseView;

import com.commitstrip.commitstripreader.common.dto.DisplayStripDto;

/**
 * This specifies the contract between the view and the presenter.
 */
public class ListFavoriteContract {

    public interface View extends BaseView<ListFavoriteContract.Presenter> {

        /**
         * Display a small message, when the user have no strip in favorite.
         */
        void noResult();

        /**
         * Display an general error message.
         */
        void showError();

        /**
         * Update list favorite with strip pass in parameter
         *
         * @param favorite strips to display
         */
        void updateListFavorite(DisplayStripDto favorite);
    }

    interface Presenter extends BasePresenter {

        /**
         * Fetch all strips. Presenter callback the view with {@code updateListFavorite}.
         */
        void fetchFavoriteStrip();
    }
}
