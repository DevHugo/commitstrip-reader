package com.commitstrip.commitstripreader.common.liststrip;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.commitstrip.commitstripreader.MyApp;
import com.commitstrip.commitstripreader.R;
import com.commitstrip.commitstripreader.common.EndlessRecyclerViewScrollListener;
import com.commitstrip.commitstripreader.common.adapter.CardStripAdapter;
import com.commitstrip.commitstripreader.common.adapter.CardStripAdapter.OnItemClickListener;
import com.commitstrip.commitstripreader.common.adapter.CardStripAdapter.OnLikeOrUnlikeItemClickListener;
import com.commitstrip.commitstripreader.common.adapter.CardStripAdapter.OnShareButtonClickListener;
import com.commitstrip.commitstripreader.common.dto.StripWithImageDto;
import com.commitstrip.commitstripreader.data.component.SharedPreferencesComponent;
import com.commitstrip.commitstripreader.fullscreen.FullScreenStripActivity;
import com.commitstrip.commitstripreader.strip.StripActivity;
import com.commitstrip.commitstripreader.util.ImageUtils;
import com.commitstrip.commitstripreader.util.converter.StripWithImageDtoToStripDto;
import com.sdsmdg.tastytoast.TastyToast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ListStripFragment extends Fragment implements ListStripContract.View {

    private final Integer NUMBER_OF_STRIP_PER_PAGE = 5;

    private ListStripContract.Presenter mPresenter;
    private Unbinder uibinder;
    private CardStripAdapter stripAdapter;

    @BindView(R.id.contentFragmentStrip) public LinearLayout mainLayout;
    @BindView(R.id.main_swipe_refresh_layout) public SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recyclerView) public RecyclerView recyclerView;
    @BindView(R.id.error_view) public LinearLayout errorView;

    @Inject ImageUtils mImageUtils;
    @Inject StripWithImageDtoToStripDto converterStripImageDtoToStripDto;
    @NonNull private EndlessRecyclerViewScrollListener scrollListener;

    public static ListStripFragment newInstance() { return new ListStripFragment(); }

    private OnItemClickListener displayStripListener = strip -> {

        if (strip != null) {

            Intent intent = new Intent(getContext(), StripActivity.class);
            intent.putExtras(StripActivity.newInstance(strip.getId()));

            startActivity(intent);
        }
    };

    private OnLikeOrUnlikeItemClickListener likeOrUnlikeListener = (strip, drawable) -> {
        if (strip.isFavorite()) {

            mPresenter.deleteFavorite(strip.getId());
        } else {

            if (drawable != null) {

                int compression = mPresenter.fetchCompressionLevelImages();

                ByteArrayOutputStream bytes = mImageUtils.transform(drawable, compression);
                mPresenter.addFavorite(strip, bytes);
            }
        }
    };

    private OnItemClickListener fullscreenListener = strip -> {

        if (strip != null && strip.getId() != null && strip.getContent() != null) {

            Intent intent = new Intent(getContext(), FullScreenStripActivity.class);
                intent.putExtras(FullScreenStripActivity.newInstance(
                        converterStripImageDtoToStripDto.apply(strip), false
                ));

            startActivity(intent);
        }
    };

    private OnShareButtonClickListener shareListener = (strip, imageView) -> {

        Drawable drawable = imageView.getDrawable();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        if (drawable != null) {
            bos = mImageUtils.transform(drawable, 100);
        }

        File newFile = mPresenter.saveSharedImageInSharedFolder(strip.getId(), bos);

        Uri contentUri = FileProvider.getUriForFile(getContext(),
                "com.commitstrip.commitstripreader",
                newFile);

        if (contentUri != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.setDataAndType(contentUri,
                    getContext().getContentResolver().getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            startActivity(Intent.createChooser(shareIntent,
                    getString(R.string.share_content_title)));
        }
    };

    /* (no-Javadoc) */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_liststrip, container, false);

        DaggerListStripFragmentComponent.builder().build().inject(this);

        uibinder = ButterKnife.bind(this, view);

        mPresenter.subscribe();

        mPresenter.fetchStrip(getNumberStripPerPage(), 0);

        stripAdapter = new CardStripAdapter(new ArrayList<>(),
                displayStripListener,
                likeOrUnlikeListener,
                fullscreenListener,
                shareListener,
                R.layout.row_liststrip);
        recyclerView.setAdapter(stripAdapter);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                page++;

                mPresenter.fetchStrip(NUMBER_OF_STRIP_PER_PAGE, page);
            }
        };

        recyclerView.addOnScrollListener(scrollListener);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            mPresenter.refreshStrip();
        });

        return view;
    }

    /* (no-Javadoc) */
    @Override
    public void setAbstractPresenter(ListStripAbstractPresenter listStripAbstractPresenter) {
        mPresenter = listStripAbstractPresenter;
    }

    /* (no-Javadoc) */
    @Override public void cancelRefreshStrip() {
        Handler mainHandler = new Handler(getContext().getMainLooper());

        mainHandler.post(() -> swipeRefreshLayout.setRefreshing(false));
    }

    /* (no-Javadoc) */
    @Override public void disableRefreshStrip() {
        Handler mainHandler = new Handler(getContext().getMainLooper());

        mainHandler.post(() -> swipeRefreshLayout.setEnabled(false));
    }

    /* (no-Javadoc) */
    @Override public void addMoreStrips(List<StripWithImageDto> moreStrips) {
        int previousSize = stripAdapter.getStrips().size() - 1;

        stripAdapter.getStrips().addAll(moreStrips);

        recyclerView.post(
                () -> stripAdapter.notifyItemRangeInserted(previousSize, moreStrips.size() - 1));
    }

    /* (no-Javadoc) */
    @Override public void addMoreStripsFromTheStart(List<StripWithImageDto> moreStrips) {

        stripAdapter.getStrips().addAll(0, moreStrips);

        recyclerView.post(() -> {
            stripAdapter.notifyItemRangeInserted(0, moreStrips.size());
        });
    }

    /* (no-Javadoc */
    @Override public boolean stripAlreadyDisplay (StripWithImageDto strip) {

        int i=0;
        while (stripAdapter.getStrips().get(i).getId().equals(strip.getId())) {
            i++;
        }

        return i == stripAdapter.getStrips().size();
    }

    /* (no-Javadoc) */
    @Override public Integer getNumberStripPerPage() {
        return NUMBER_OF_STRIP_PER_PAGE;
    }

    /* (non-Javadoc) */
    @Override public void displayErrorAddFavorite() {

        Context context;
        if (getActivity() != null) {
            context = getActivity().getApplicationContext();
        } else {
            context = getContext();
        }

        TastyToast.makeText(
                context,
                getString(R.string.error_add_favorite),
                TastyToast.LENGTH_LONG,
                TastyToast.ERROR);
    }

    /* (no-Javadoc) */
    @Override public void clearStripDisplayed() {
        int size = stripAdapter.getStrips().size();

        if (size > 0) {
            for (int i = 0; i < size - 1; i++) {
                stripAdapter.getStrips().remove(0);
            }

            recyclerView.post(() -> stripAdapter.notifyDataSetChanged());
        }

        scrollListener.resetState();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mPresenter != null) {
            mPresenter.subscribe();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mPresenter != null) {
            mPresenter.unsubscribe();
        }
    }

    /* (no-Javadoc) */
    @Override public void onDestroyView() {
        super.onDestroyView();

        if (uibinder != null) {
            uibinder.unbind();
        }

        if (mPresenter != null) {
            mPresenter.unsubscribe();
        }
    }

}
