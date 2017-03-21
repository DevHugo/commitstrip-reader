package com.commitstrip.commitstripreader.common.adapter;

import android.support.annotation.NonNull;
import android.support.v7.util.SortedList;
import android.support.v7.util.SortedList.Callback;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.commitstrip.commitstripreader.R;
import com.commitstrip.commitstripreader.common.dto.DisplayStripDto;
import com.commitstrip.commitstripreader.util.Preconditions;

public class SortedStripByDateAdapter extends RecyclerView.Adapter<SortedStripByDateAdapter.ViewHolder> {

    /**
     * Callback on touch on the event on item.
     */
    public interface OnItemClickListener {
        void onItemClick(DisplayStripDto item);
    }

    @NonNull
    private final SortedList<DisplayStripDto> strips = new SortedList<>(DisplayStripDto.class,
            new Callback<DisplayStripDto>() {

                @Override
                public int compare(DisplayStripDto one, DisplayStripDto other) {

                    if (one == other) return 0;
                    if (other == null) return -1;

                    if (other.getReleaseDate().equals(one.getReleaseDate())) {
                        return 0;
                    } else if (other.getReleaseDate().after(one.getReleaseDate())){
                        return 1;
                    }
                    else {
                        return -1;
                    }
                }

                @Override
                public boolean areContentsTheSame(DisplayStripDto oldItem, DisplayStripDto newItem) {
                    return oldItem.equals(newItem);
                }

                @Override
                public boolean areItemsTheSame(DisplayStripDto one, DisplayStripDto other) {

                    if (one == other) return true;
                    if (other == null || one.getClass() != other.getClass()) return false;

                    return one.getId() != null && other.getId() != null &&
                            one.getId().equals(other.getId());
                }

                @Override
                public void onInserted(int position, int count) {
                    notifyItemRangeInserted(position, count);
                }

                @Override
                public void onChanged(int position, int count) {
                    notifyItemRangeChanged(position, count);
                }

                @Override
                public void onRemoved(int position, int count) {
                    notifyItemRangeRemoved(position, count);
                }

                @Override
                public void onMoved(int fromPosition, int toPosition) {
                    notifyItemMoved(fromPosition, toPosition);
                }
            });

    @NonNull private final OnItemClickListener mDisplayDetailStripListener;
    @NonNull private final Integer mLayout;

    /**
     * Construct of strip adapter.
     *
     * @param displayDetailStripListener
     * @param layout target layout for the strip.
     */
    public SortedStripByDateAdapter(@NonNull OnItemClickListener displayDetailStripListener,
            @NonNull Integer layout) {

        Preconditions.checkNotNull(displayDetailStripListener);
        Preconditions.checkNotNull(layout);

        mDisplayDetailStripListener = displayDetailStripListener;
        mLayout = layout;
    }

    /* (non-Javadoc) */
    @Override
    public SortedStripByDateAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(mLayout, viewGroup, false);
        return new ViewHolder(view);
    }

    /* (non-Javadoc) */
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) { viewHolder.bind(strips.get(i)); }

    /* (non-Javadoc) */
    @Override
    public int getItemCount() {
        return strips.size();
    }

    public void add(DisplayStripDto strip) {
        strips.add(strip);
    }

    /**
     * Return the dislayed list of strips
     */
    @NonNull
    public SortedList<DisplayStripDto> getStrips () {
        return strips;
    }

    /* (non-Javadoc) */
    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private ImageView imgStrip;

        /* (non-Javadoc) */
        ViewHolder(@NonNull View view) {
            super(view);

            Preconditions.checkNotNull(view);

            title = (TextView) view.findViewById(R.id.title);
            imgStrip = (ImageView) view.findViewById(R.id.img_strip);
        }

        /* (non-Javadoc) */
        void bind(DisplayStripDto strip) {

            title.setText(strip.getTitle());
            strip.getImageRequestCreator().into(imgStrip);

            itemView.setOnClickListener(v -> mDisplayDetailStripListener.onItemClick(strip));
        }

    }
}
