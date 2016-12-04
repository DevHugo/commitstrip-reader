package com.commitstrip.commitstripreader.listfavorite;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.commitstrip.commitstripreader.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ListFavoriteAdapter extends RecyclerView.Adapter<ListFavoriteAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(ListFavoriteDto item);
    }

    private List<ListFavoriteDto> favorites;
    private final OnItemClickListener listener;

    public ListFavoriteAdapter(List<ListFavoriteDto> favorites, OnItemClickListener listener) {
        this.favorites = favorites;
        this.listener = listener;
    }

    @Override
    public ListFavoriteAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_listfavorite, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.bind(favorites.get(i), listener);
    }

    @Override
    public int getItemCount() {
        return favorites.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView title;
        private ImageView img_strip;

        public ViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.title);
            img_strip = (ImageView) view.findViewById(R.id.img_strip);
        }

        public void bind(ListFavoriteDto favorite, OnItemClickListener listener) {

            title.setText(favorite.getTitle());
            favorite.getImageRequestCreator().into(img_strip);
            itemView.setOnClickListener(v -> listener.onItemClick(favorite));
        }

    }
}
