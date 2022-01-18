package com.itible.exercise;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ExerciseVH extends RecyclerView.ViewHolder {
    public TextView txt_max, txt_sum, txt_date, txt_reps;
    public ImageView icon_progress;


    public ExerciseVH(@NonNull View itemView) {
        super(itemView);
        txt_max = itemView.findViewById(R.id.txt_max);
        txt_sum = itemView.findViewById(R.id.txt_sum);
        txt_date = itemView.findViewById(R.id.txt_date);
        icon_progress = itemView.findViewById(R.id.icon_progress);
        txt_reps = itemView.findViewById(R.id.txt_reps);
    }

}