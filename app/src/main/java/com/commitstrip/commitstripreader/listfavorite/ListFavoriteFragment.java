package com.commitstrip.commitstripreader.listfavorite;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.commitstrip.commitstripreader.R;
import com.commitstrip.commitstripreader.common.adapter.SortedStripByDateAdapter;
import com.commitstrip.commitstripreader.common.adapter.SortedStripByDateAdapter.OnItemClickListener;
import com.commitstrip.commitstripreader.common.dto.DisplayStripDto;
import com.commitstrip.commitstripreader.displayfavorite.DisplayFavoriteStripActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ListFavoriteFragment extends Fragment implements ListFavoriteContract.View {

    private ListFavoriteContract.Presenter mPresenter;

    private SortedStripByDateAdapter mAdapter;

    public static ListFavoriteFragment newInstance() {
        return new ListFavoriteFragment();
    }

    private Unbinder uibinder;

    @BindView(R.id.recyclerView) RecyclerView recyclerView;

    @BindView(R.id.error_view) LinearLayout errorView;

    @BindView(R.id.error_text) TextView textView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_listfavorite, container, false);

        uibinder = ButterKnife.bind(this, view);

        mPresenter.subscribe();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        OnItemClickListener onItemClickListener = item -> {
            Intent intent = new Intent(getContext(), DisplayFavoriteStripActivity.class);
            intent.putExtras(DisplayFavoriteStripActivity.newInstance(item.getId()));

            startActivity(intent);
        };

        mAdapter = new SortedStripByDateAdapter(onItemClickListener, R.layout.row_listfavorite);
        recyclerView.setAdapter(mAdapter);

        recyclerView.setHasFixedSize(true);

        mPresenter.fetchFavoriteStrip();

        return view;
    }

    @Override
    public void setPresenter(ListFavoriteContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void updateListFavorite(DisplayStripDto favorite) {

        mAdapter.add(favorite);

        recyclerView.setVisibility(View.VISIBLE);
        errorView.setVisibility(View.GONE);
    }

    @Override
    public void noResult() {
        recyclerView.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);

        textView.setText(getString(R.string.no_fav_available));
    }

    @Override
    public void showError() {
        recyclerView.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);

        textView.setText(getString(R.string.error_fetch_strips));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (uibinder != null) {
            uibinder.unbind();
        }

        if (mPresenter != null) {
            mPresenter.unsubscribe();
        }
    }

}
