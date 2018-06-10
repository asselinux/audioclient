package ru.veider.audioclient.audioclient.recycler;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ru.veider.audioclient.audioclient.R;

public class MediaModelAdapter extends RecyclerView.Adapter<MediaModelAdapter.ModelHolder> {

    private final List<MediaModel> models = new ArrayList<>();

    @NonNull
    @Override
    public ModelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_model, parent, false);
        return new ModelHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaModelAdapter.ModelHolder holder, int position) {
        MediaModel model = models.get(position);

        Picasso.get().load(model.getImage()).into(holder.image);
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

    static class ModelHolder extends RecyclerView.ViewHolder {

        public final ImageView image;
        public final TextView name;


        private ModelHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_model_image);
            name = itemView.findViewById(R.id.item_model_name);
        }
    }
}

