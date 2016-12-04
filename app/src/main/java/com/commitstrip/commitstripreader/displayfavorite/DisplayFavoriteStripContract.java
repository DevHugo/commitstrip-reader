package com.commitstrip.commitstripreader.displayfavorite;

import com.commitstrip.commitstripreader.common.AbstractDisplayStripContract;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface DisplayFavoriteStripContract extends AbstractDisplayStripContract {

    interface View extends AbstractDisplayStripContract.View {}

    interface Presenter extends AbstractDisplayStripContract.Presenter {
        Long askForNextIdStrip();
        Long askForPreviousIdStrip();
    }
}
