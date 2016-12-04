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
import com.commitstrip.commitstripreader.displayfavorite.DisplayFavoriteStripActivity;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ListFavoriteFragment extends Fragment implements ListFavoriteContract.View {

    private ListFavoriteContract.Presenter mPresenter;

    private List<ListFavoriteDto> favorites;
    private ListFavoriteAdapter adapter;


    @Override
    public void setPresenter(ListFavoriteContract.Presenter presenter) {
        mPresenter = presenter;
    }

    public static ListFavoriteFragment newInstance() {
        return new ListFavoriteFragment();
    }

    private Unbinder uibinder;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.error_view)
    LinearLayout errorView;

    @BindView(R.id.error_text)
    TextView textView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_listfavorite, container, false);

        uibinder = ButterKnife.bind(this, view);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        favorites = new ArrayList<>();

        adapter = new ListFavoriteAdapter(favorites, item -> {
            Intent intent = new Intent(getContext(), DisplayFavoriteStripActivity.class);
            intent.putExtra(DisplayFavoriteStripActivity.ARGUMENT_STRIP_ID, item.getId());

            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        mPresenter.fetchFavoriteStrip();

        return view;
    }

    @Override
    public void updateListFavorite(ListFavoriteDto favorite) {

        favorites.add(favorite);

        if (favorites.size() == 1)
            adapter.notifyItemInserted(favorites.size()-1);
        else
            adapter.notifyItemInserted(1);

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

        uibinder.unbind();
        mPresenter.unsubscribe();
    }

}
