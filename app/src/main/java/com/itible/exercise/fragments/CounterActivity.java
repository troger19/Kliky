package com.itible.exercise.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.snackbar.Snackbar;
import com.itible.exercise.MainActivity;
import com.itible.exercise.MyPreferencesActivity;
import com.itible.exercise.R;
import com.itible.exercise.entity.Exercise;
import com.itible.exercise.entity.ExerciseDao;
import com.itible.exercise.entity.Statistics;
import com.itible.exercise.entity.StatisticsDao;
import com.itible.exercise.util.Util;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

import at.grabner.circleprogress.AnimationState;
import at.grabner.circleprogress.AnimationStateChangedListener;
import at.grabner.circleprogress.CircleProgressView;

public class CounterActivity extends FragmentActivity {

    private final static String LOG_TAG = CounterActivity.class.getSimpleName();
    private final StringBuilder repsBuilder = new StringBuilder();
    CoordinatorLayout coordinatorLayout;
    private int NUMBER_OF_SERIES, PAUSE_IN_SECONDS;
    private String dialogText;
    private int counter = 0;
    private boolean wasAnimating = false;
    private CircleProgressView progressCircle;
    private TextView txtRepsArray;
    private ExerciseDao exerciseDao;
    private StatisticsDao statisticsDao;
    private Statistics statistics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        statistics = (Statistics) getIntent().getSerializableExtra("Statistics");
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.bell_start);

        progressCircle = findViewById(R.id.circleView);
        txtRepsArray = findViewById(R.id.txtReps);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);

        exerciseDao = new ExerciseDao();
        statisticsDao = new StatisticsDao();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        NUMBER_OF_SERIES = Integer.parseInt(sharedPref.getString(MyPreferencesActivity.NUMBER_OF_SERIES_PREF, "10"));
        PAUSE_IN_SECONDS = Integer.parseInt(sharedPref.getString(MyPreferencesActivity.PAUSE_PREF, "60"));


        progressCircle.setOnClickListener(v -> {
            counter++;
            progressCircle.setValueAnimated(0, 100, PAUSE_IN_SECONDS * 1000);
            showEnterRepsDialog();
        });

        progressCircle.setOnAnimationStateChangedListener(new AnimationStateChangedListener() {
            @Override
            public void onAnimationStateChanged(AnimationState _animationState) {
                Log.d(LOG_TAG, "onAnimationStateChanged: " + _animationState);
                if (_animationState == AnimationState.ANIMATING) {
                    wasAnimating = true;
                }

                if (wasAnimating && _animationState == AnimationState.IDLE && counter < NUMBER_OF_SERIES) {
                    Log.d(LOG_TAG, "DONE");
                    mp.setVolume(0.5f, 0.5f);
                    mp.start();
                }
                if (counter == NUMBER_OF_SERIES) {
                    progressCircle.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void showEnterRepsDialog() {
        AlertDialog.Builder enterRepsDialog = new AlertDialog.Builder(this);
        enterRepsDialog.setTitle("Insert txtReps");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.requestFocus();
        enterRepsDialog.setView(input);
        enterRepsDialog.setCancelable(false);

        enterRepsDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogText = input.getText().toString();
                repsBuilder.append(dialogText).append(",");
                txtRepsArray.setText(repsBuilder);
                if (counter == NUMBER_OF_SERIES) {
                    showDoneSnackBar();
                }

            }
        });
        enterRepsDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog d = enterRepsDialog.create();
        d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        d.show();
    }


    private void showSaveTrainingDialog() {
        AlertDialog.Builder saveTrainingDialog = new AlertDialog.Builder(this);
        saveTrainingDialog.setTitle("Save training");
        saveTrainingDialog.setCancelable(false);
        final EditText editTextReps = new EditText(this);
        editTextReps.setText(txtRepsArray.getText());
        editTextReps.requestFocus();
        editTextReps.setInputType(InputType.TYPE_CLASS_NUMBER);
        saveTrainingDialog.setView(editTextReps);

        saveTrainingDialog.setPositiveButton("Save", (dialog, which) -> {
            saveToDb(Util.removeLastComma(editTextReps.getText().toString()));
            Intent intent = new Intent(CounterActivity.this, MainActivity.class);
            startActivity(intent);
        });
        saveTrainingDialog.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog d = saveTrainingDialog.create();
        d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        d.show();
    }

    private void showDoneSnackBar() {
        Snackbar bar = Snackbar.make(coordinatorLayout, "Training Complete", Snackbar.LENGTH_INDEFINITE)
                .setAction("Save", v -> showSaveTrainingDialog());
        bar.show();
    }

    private void saveToDb(String repsToSave) {
        String[] repetitions = repsToSave.split(",");
        int sum = 0;
        int max = 0;
        int i;
        for (String repetition : repetitions) {
            try {
                i = Integer.parseInt(repetition);
                sum += i;
                if (i > max) {
                    max = i;
                }
            } catch (Exception e) {
                Log.i(LOG_TAG, "this is not a number " + repetition);
            }
        }

        Exercise exercise = new Exercise();
        long date = Calendar.getInstance().getTime().getTime();
        exercise.setDate(date);
        exercise.setReps(Arrays.toString(repetitions));
        exercise.setSum(sum);
        exercise.setMax(max);

        exerciseDao.add(exercise)
                .addOnSuccessListener(suc -> {
                    Toast.makeText(this, "Record is inserted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(er ->
                        Toast.makeText(this, "" + er.getMessage(), Toast.LENGTH_SHORT).show());

        if (statistics == null) { // 1. run of application
            Statistics statistics = new Statistics();
            statistics.setMaxReps(max);
            statistics.setMaxSum(sum);
            statisticsDao.add(statistics)
                    .addOnSuccessListener(suc -> {
                        Toast.makeText(this, "Statistic record is inserted", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(er ->
                            Toast.makeText(this, "" + er.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            if (sum > statistics.getMaxSum() || max > statistics.getMaxReps()) { // if record is made
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("maxReps", statistics.getMaxReps());
                hashMap.put("maxSum", statistics.getMaxSum());
                statisticsDao.update(statistics.getKey(), hashMap)
                        .addOnSuccessListener(suc -> Toast.makeText(this, "Record is updated", Toast.LENGTH_SHORT).show()).addOnFailureListener(er -> Toast.makeText(this, "" + er.getMessage(), Toast.LENGTH_SHORT).show());
            }
            finish();
            Intent intent = new Intent(CounterActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

}

