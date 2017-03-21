package com.commitstrip.commitstripreader.fullscreen;

import com.commitstrip.commitstripreader.BasePresenter;
import com.commitstrip.commitstripreader.BaseView;
import com.commitstrip.commitstripreader.common.dto.DisplayStripDto;
import com.commitstrip.commitstripreader.dto.StripDto;
import com.squareup.picasso.RequestCreator;

public class FullScreenStripContrat {


    interface View {

        /**
         * Display on the screen, the strip pass in parameter.
         *
         * @param mStrip strip
         */
        void askForDisplayImage(StripDto mStrip);
    }

    interface Presenter extends BasePresenter {

        /**
         * Construct image strip.
         *
         * @param id strip id
         * @param url url where the strip image is saved
         * @return picasso instance
         */
        RequestCreator getImageStrip(Long id, String url);

        /**
         * Fetch strip pass in parameter.
         *
         * @param id strip id
         * @return strip
         */
        void fetchStrip(Long id);

        void onSwipeRight();

        void onSwipeLeft();
    }

}
