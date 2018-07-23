package ru.veider.audioclient.audioclient.fragments;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import permissions.dispatcher.NeedsPermission;
import ru.veider.audioclient.audioclient.R;
import ru.veider.audioclient.audioclient.fragments.dummy.MediaModel;

import java.util.ArrayList;
import java.util.List;


public class AudioLibraryRecyclerViewAdapter extends RecyclerView.Adapter<AudioLibraryRecyclerViewAdapter.ModelHolder> {

    private final List<MediaModel> models = new ArrayList<>();

    private OnItemClickListener onItemClickListener;

    public AudioLibraryRecyclerViewAdapter(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    @NonNull
    public ModelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_model, parent, false);//warning
        final ModelHolder holder = new ModelHolder(itemView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onItemClickListener != null){
                    MediaModel model = models.get(position);
                    onItemClickListener.onItemClick(model, position);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ModelHolder holder, int position) {
        MediaModel model = models.get(position);
        Picasso.get()
                .load(model.getImage())
                .into(holder.image);
        holder.name.setText(model.getName());
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public void replaceAll(List<MediaModel> newItems) {
        this.models.clear();
        this.models.addAll(newItems);
        notifyDataSetChanged();
    }

    public static class ModelHolder extends RecyclerView.ViewHolder {

        public final ImageView image;
        public final TextView name;

        public ModelHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_model_image);
            name = itemView.findViewById(R.id.item_model_name);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(@NonNull MediaModel mediaModel, int position);
    }
}
