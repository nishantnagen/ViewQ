package com.digipodium.viewq.viewq;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.TextAnnotation;

import java.util.List;


public class TextAdapter extends RecyclerView.Adapter<TextAdapter.Holder> {


    Context context;
    int layout;
    List<EntityAnnotation> textlist;
    LayoutInflater inflater;

    public TextAdapter(Context context, int layout, List<EntityAnnotation> labellist) {
        this.context = context;
        this.layout = layout;
        this.textlist = labellist;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(layout, parent, false);
        return new Holder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        EntityAnnotation entityAnnotation = (EntityAnnotation) textlist.get(position);
        holder.image_details.setText(entityAnnotation.getDescription());
    }

    @Override
    public int getItemCount() {
        return textlist.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView image_details;

        public Holder(View itemView) {
            super(itemView);
            image_details = itemView.findViewById(R.id.image_details);

        }
    }
}
