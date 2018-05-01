package com.digipodium.viewq.viewq;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.FaceAnnotation;

import java.util.List;

public class FaceAdapter extends RecyclerView.Adapter<FaceAdapter.Holder> {

    Context context;
    int layout;
    List<FaceAnnotation> textlist;
    LayoutInflater inflater;
    int layout1;

    public FaceAdapter(Context context, int layout, List<FaceAnnotation> labellist) {
        this.context = context;
        this.layout = layout;
        this.textlist = labellist;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public FaceAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(layout, parent, false);
        return new FaceAdapter.Holder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull FaceAdapter.Holder holder, int position) {
        FaceAnnotation faceAnnotation = textlist.get(position);
      //holder.image_details.setText(faceAnnotation.);
        holder.txt_8.setText(faceAnnotation.getBlurredLikelihood());
        holder.txt_2.setText(faceAnnotation.getHeadwearLikelihood());
        holder.txt_3.setText(faceAnnotation.getJoyLikelihood());
        holder.txt_4.setText(faceAnnotation.getSorrowLikelihood());
        holder.txt_5.setText(faceAnnotation.getSurpriseLikelihood());
        holder.txt_6.setText(faceAnnotation.getUnderExposedLikelihood());
        holder.txt_7.setText(faceAnnotation.getAngerLikelihood());
        holder.txt_1.setText("FACE" + position  );
    }

    @Override
    public int getItemCount() {
        return textlist.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        TextView txt_1;
        TextView txt_2;
        TextView txt_3;
        TextView txt_4;
        TextView txt_5;
        TextView txt_6;
        TextView txt_7;
        TextView txt_8;

        public Holder(View itemView) {
            super(itemView);

            txt_1 = itemView.findViewById(R.id.txt_1);
            txt_2 = itemView.findViewById(R.id.txt_2);
            txt_3 = itemView.findViewById(R.id.txt_3);
            txt_4 = itemView.findViewById(R.id.txt_4);
            txt_5 = itemView.findViewById(R.id.txt_5);
            txt_6 = itemView.findViewById(R.id.txt_6);
            txt_7 = itemView.findViewById(R.id.txt_7);
            txt_8 = itemView.findViewById(R.id.txt_8);

        }
    }
}
