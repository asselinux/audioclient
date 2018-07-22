package ru.veider.audioclient.audioclient.fragments;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.veider.audioclient.audioclient.R;
import ru.veider.audioclient.audioclient.fragments.dummy.DummyContent.DummyItem;
import ru.veider.audioclient.audioclient.recycler.MediaModel;
import ru.veider.audioclient.audioclient.recycler.MediaModelAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link AudioLibraryFragment.OnItemClickListener}.
 *  Replace the implementation with code for your data type.
 */
public class AudioLibraryRecyclerViewAdapter extends RecyclerView.Adapter<AudioLibraryRecyclerViewAdapter.ViewHolder> {

    private final List<MediaModel> models = new ArrayList<>();

    private MediaModelAdapter.OnItemClickListener onItemClickListener;

    public AudioLibraryRecyclerViewAdapter(MediaModelAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_audiolibrary, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_model, parent, false);
        final MediaModelAdapter.ModelHolder holder = new MediaModelAdapter.ModelHolder(itemView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onItemClickListener != null) {
                    MediaModel model = models.get(position);
                    onItemClickListener.onItemClick(model, position); //ActivityNotFoundException
                }
            }
        });
        return holder;
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public DummyItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
