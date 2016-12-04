package com.commitstrip.commitstripreader.strip;

import com.commitstrip.commitstripreader.BasePresenter;
import com.commitstrip.commitstripreader.BaseView;
import com.commitstrip.commitstripreader.common.AbstractDisplayStripContract;
import com.commitstrip.commitstripreader.common.AbstractDisplayStripPresenter;
import com.squareup.picasso.RequestCreator;

import java.util.Date;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface StripContract extends AbstractDisplayStripContract {

    interface View extends AbstractDisplayStripContract.View {}

    interface Presenter extends AbstractDisplayStripContract.Presenter {
        Long askForNextIdStrip();
        Long askForPreviousIdStrip();
    }
}
