package com.itible.exercise;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.itible.exercise.entity.Exercise;
import com.itible.exercise.entity.ExerciseDao;
import com.itible.exercise.util.Util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

public class RVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    private final ExerciseDao exerciseDao;
    ArrayList<Exercise> list = new ArrayList<>();
    int arrowDown, arrowUp;


    public RVAdapter(Context ctx, int arrowUp, int arrowDown, ExerciseDao exerciseDao) {
        this.context = ctx;
        this.arrowUp = arrowUp;
        this.arrowDown = arrowDown;
        this.exerciseDao = exerciseDao;
    }

    public void setItems(ArrayList<Exercise> emp) {
        list.addAll(emp);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.training_item, parent, false);
        return new ExerciseVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Exercise e = null;
        this.onBindViewHolder(holder, position, e);
    }

    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, Exercise e) {
        ExerciseVH viewHolder = (ExerciseVH) holder;
        Exercise emp = e == null ? list.get(position) : e;
        viewHolder.txt_max.setText(String.valueOf(emp.getMax()));
        viewHolder.txt_sum.setText(String.valueOf(emp.getSum()));
        viewHolder.txt_date.setText(Util.sdf.format(new Date(emp.getDate())));
        viewHolder.txt_reps.setText(String.valueOf(emp.getReps()));
        viewHolder.txt_date.setOnClickListener(v -> {
            showEditTrainingDialog(emp);
        });

        Exercise maxRepExercise = list
                .stream()
                .max(Comparator.comparing(Exercise::getMax))
                .orElse(new Exercise());

        Exercise maxSumExercise = list
                .stream()
                .max(Comparator.comparing(Exercise::getSum))
                .orElse(new Exercise());


        if (position > 0) {
            Exercise pastMax = list.subList(0, position).stream().max(Comparator.comparing(Exercise::getMax)).orElse(new Exercise());
            if (Integer.parseInt(viewHolder.txt_max.getText().toString()) > pastMax.getMax()) {
                viewHolder.icon_progress.setImageDrawable(context.getResources().getDrawable(arrowUp, context.getTheme()));
            } else {
                viewHolder.icon_progress.setImageDrawable(context.getResources().getDrawable(arrowDown, context.getTheme()));
            }
        }
        if (Integer.valueOf(viewHolder.txt_max.getText().toString()) == maxRepExercise.getMax()) {
            viewHolder.txt_max.setTextColor(context.getResources().getColor(R.color.colorCounterPositive));
        }
        if (Integer.valueOf(viewHolder.txt_sum.getText().toString()) == maxSumExercise.getSum()) {
            viewHolder.txt_sum.setTextColor(context.getResources().getColor(R.color.colorCounterPositive));
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void showEditTrainingDialog(Exercise exercise) {
        LayoutInflater factory = LayoutInflater.from(context);
        final View textEntryView = factory.inflate(R.layout.edit_exercise_layout, null);

        final EditText edtDate = textEntryView.findViewById(R.id.edt_date);
        final EditText edtMax = textEntryView.findViewById(R.id.edt_max_rep);
        final EditText edtSum = textEntryView.findViewById(R.id.edt_max_sum);
        final EditText edtReps = textEntryView.findViewById(R.id.edt_reps);

        edtDate.setText(Util.sdf.format(exercise.getDate()), TextView.BufferType.EDITABLE);
        edtMax.setText(String.valueOf(exercise.getMax()), TextView.BufferType.EDITABLE);
        edtSum.setText(String.valueOf(exercise.getSum()), TextView.BufferType.EDITABLE);
        edtReps.setText(String.valueOf(exercise.getReps()), TextView.BufferType.EDITABLE);

        Dialog editTrainingDialog = new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Edit Values:").setView(textEntryView)
                .setPositiveButton("Save", (dialog, which) -> {
                    if (!Util.insertedValuesCheck(edtMax.getText().toString(), edtSum.getText().toString(), edtReps.getText().toString())) {
                        Toast.makeText(context, "Zadane hodnoty nie su v poriadku", Toast.LENGTH_LONG).show();
                        return;
                    }
                    HashMap<String, Object> hashMap = new HashMap<>();
                    try {
                        hashMap.put("date", Util.sdf.parse(edtDate.getText().toString()).getTime());
                        hashMap.put("max", Integer.valueOf(edtMax.getText().toString()));
                        hashMap.put("sum", Integer.valueOf(edtSum.getText().toString()));
                        hashMap.put("reps", String.valueOf(edtReps.getText()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    exerciseDao.update(exercise.getKey(), hashMap)
                            .addOnSuccessListener(suc -> Toast.makeText(context, "Record is updated", Toast.LENGTH_SHORT).show()).addOnFailureListener(er -> Toast.makeText(context, "" + er.getMessage(), Toast.LENGTH_SHORT).show());

                    Intent intent = new Intent(context, MainActivity.class);
                    context.startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                .create();

        editTrainingDialog.setCanceledOnTouchOutside(false);
        editTrainingDialog.show();
    }


}