package com.commitstrip.commitstripreader.common.displaystrip;

import android.support.annotation.NonNull;
import android.view.KeyEvent;

import com.commitstrip.commitstripreader.BasePresenter;
import com.commitstrip.commitstripreader.common.dto.StripWithImageDto;
import com.commitstrip.commitstripreader.dto.StripDto;
import com.squareup.picasso.RequestCreator;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface DisplayStripContract {

    interface View {

        /**
         * Configure initial state for the like icon.
         *
         * @param favorite True, if the strip is one of this favorite, false otherwise.
         */
        void displayIconIsFavorite(boolean favorite);

        /* (non-Javadoc) */
        void setAbstractPresenter(
                @NonNull DisplayStripAbstractPresenter abstractDisplayStripPresenter);

        /**
         * When the user press the key for increasing the volume.
         *
         * @return True, if the event have been processed, false otherwise.
         */
        boolean onKeyVolumeUp();

        /**
         * When the user press the key for decreasing the volume.
         *
         * @return True, if the event have been processed, false otherwise.
         */
        boolean onKeyVolumeDown();

        /**
         * Display a toast error with a message explaining that we can not add the favorite.
         */
        void displayErrorAddFavorite();

        /**
         * Display a toast error with a message explaining that we can not fetch strip.
         */
        void displayErrorFetchStrip();

        /**
         * Display strip
         *
         * @param requestCreator picasso instance
         * @param strip strip with image dto
         */
        void displayStrip(StripDto strip, RequestCreator requestCreator);
    }

    /* (non-Javadoc) */
    interface Presenter extends BasePresenter {

        /**
         * Return the next most recent strip for the strip displayed on the screen.
         *
         * @return next most recent strip
         */
        void onSwipeLeft();

        /**
         * Return the previous most recent strip for the strip displayed on the screen.
         *
         * @return previous most recent strip
         */
        void onSwipeRight();

        /* (no-Javadoc) */
        boolean shouldUpdateNextStripOnFullScreen();

        /* (no-Javadoc) */
        void onStripDisplayed(StripDto strip);

        /**
         * Add current strip displayed on screen as a favorite.
         *
         * @param byteArrayOutputStream
         */
        void addFavorite(@NonNull ByteArrayOutputStream byteArrayOutputStream);

        /**
         * Delete current strip displayed on screen as a favorite.
         */
        void deleteFavorite();

        File saveSharedImageInSharedDirectory(Long id, ByteArrayOutputStream bos);

        /**
         * @return true, if the strip is one of this favorite
         */
        boolean isFav();

        /**
         * Get compression level image
         *
         * @return compression level
         */
        int getCompressionLevelImages();

        /**
         * Fetch who got priority for using volume key
         *
         * @return true, commitstrip app got priority, otherwise it false.
         */
        boolean fetchPriorityForUseVolumeKey();
    }

}
