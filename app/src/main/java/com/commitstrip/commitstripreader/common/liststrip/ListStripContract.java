package com.commitstrip.commitstripreader.common.liststrip;

import com.commitstrip.commitstripreader.BasePresenter;
import com.commitstrip.commitstripreader.common.dto.StripWithImageDto;
import com.commitstrip.commitstripreader.dto.StripDto;

import io.reactivex.Flowable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface ListStripContract {

    interface View {

        /**
         * @param listStripAbstractPresenter presenter
         */
        void setAbstractPresenter(ListStripAbstractPresenter listStripAbstractPresenter);

        /**
         * Abort refresh strip.
         */
        void cancelRefreshStrip();

        /* (no-Javadoc) */
        void disableRefreshStrip();

        /**
         * Add more strips at the end of the list displayed.
         *
         * @param moreStrips
         */
        void addMoreStrips(List<StripWithImageDto> moreStrips);

        /**
         * Add more strips from the start of the list displayed.
         *
         * @param moreStrips
         */
        void addMoreStripsFromTheStart(List<StripWithImageDto> moreStrips);

        /**
         * Check if the strip is not already displayed
         *
         * @param strip
         */
        boolean stripAlreadyDisplay(StripWithImageDto strip);

        /**
         * Number of strips per page. During the scroll, a fictive number of page is set.
         * The returned number is the number of strip per page.
         *
         * @return number of strips per page
         */
        Integer getNumberStripPerPage();

        /**
         * Clear all strips displayed on screen.
         */
        void clearStripDisplayed();

        /**
         * Display a toast error with a message explaining that we can not add the favorite.
         */
        void displayErrorAddFavorite();
    }

    interface Presenter extends BasePresenter {

        /**
         * Ask for a refresh all strips displayed
         */
        void refreshStrip();

        /**
         * Fetch strips on a non ui thread. Result will be add with the addMoreStrips and
         * addMoreStripsFromTheStart method.
         *
         * @param numberOfStripPerPage Number of strips wanted
         * @param page
         */
        void fetchStrip(Integer numberOfStripPerPage, int page);

        /**
         * Add strip passed in parameter in the user favorite.
         *
         * @param strip to add in favorite
         * @param bytes an array of bytes for the image representation.
         */
        void addFavorite(StripWithImageDto strip, ByteArrayOutputStream bytes);

        /**
         * Delete favorite passed in parameter in the user favorite.
         *
         * @param strip
         */
        void deleteFavorite(Long strip);

        /**
         * Fetch level image compression
         *
         * @return compression level from 0 (bad quality) to 100 (high quality)
         */
        int fetchCompressionLevelImages();

        File saveSharedImageInSharedFolder(Long id, ByteArrayOutputStream bos);

        /* (no-Javadoc) */
        File saveSharedImage(Long id, ByteArrayOutputStream stream);
    }
}
