package com.babel.kliky.fragments;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.babel.kliky.MainActivity;
import com.babel.kliky.MyPreferencesActivity;
import com.babel.kliky.R;
import com.babel.kliky.entity.Kliky;
import com.babel.kliky.util.DatabaseHelper;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import at.grabner.circleprogress.AnimationState;
import at.grabner.circleprogress.AnimationStateChangedListener;
import at.grabner.circleprogress.CircleProgressView;

public class CounterActivity extends FragmentActivity {

    private final static String LOG_TAG = CounterActivity.class.getSimpleName();
    private int NUMBER_OF_SERIES, PAUSE_IN_SECONDS;

    private String dialogText;
    private int counter = 0;
    private boolean wasAnimating = false;
    private CircleProgressView progressCircle;
    private TextView txtRepsArray;
    private StringBuilder repsBuilder = new StringBuilder();
    CoordinatorLayout coordinatorLayout;
    private ContentResolver contentResolver;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        final MediaPlayer mp = MediaPlayer.create(this, R.raw.bell_start);

        progressCircle = (CircleProgressView) findViewById(R.id.circleView);
        txtRepsArray = (TextView) findViewById(R.id.txtReps);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        contentResolver = CounterActivity.this.getContentResolver();
        databaseHelper =new DatabaseHelper(getApplicationContext());

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        NUMBER_OF_SERIES = Integer.valueOf(sharedPref.getString(MyPreferencesActivity.NUMBER_OF_SERIES_PREF, "10"));
        PAUSE_IN_SECONDS = Integer.valueOf(sharedPref.getString(MyPreferencesActivity.PAUSE_PREF, "60"));


        progressCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter++;
                progressCircle.setValueAnimated(0, 100, PAUSE_IN_SECONDS * 1000);
                showEnterRepsDialog();
            }
        });

        progressCircle.setOnAnimationStateChangedListener(new AnimationStateChangedListener() {
            @Override
            public void onAnimationStateChanged(AnimationState _animationState) {
                Log.d(LOG_TAG, "onAnimationStateChanged: " + _animationState);
                if (_animationState == AnimationState.ANIMATING) {
                    wasAnimating = true;
                }

                if (wasAnimating && _animationState == AnimationState.IDLE && counter < NUMBER_OF_SERIES) {
                    Log.d(LOG_TAG, "HOTOVO");
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Insert txtReps");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog d = builder.create();
        d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        d.show();
    }


    private void showSaveTrainingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Save training");
        final EditText editTextReps = new EditText(this);
        editTextReps.setText(txtRepsArray.getText());
        editTextReps.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(editTextReps);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveToDb(removeLastComma(editTextReps.getText().toString()));
                Intent intent = new Intent(CounterActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog d = builder.create();
        d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        d.show();
    }


    private void showDoneSnackBar() {
        Snackbar bar = Snackbar.make(coordinatorLayout, "Training Complete", Snackbar.LENGTH_INDEFINITE)
                .setAction("Save", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showSaveTrainingDialog();
                    }
                });
        TextView tv = (TextView) bar.getView().findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.CYAN);
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

        Kliky kliky = new Kliky();
        Date date = Calendar.getInstance().getTime();
        kliky.setDate(date);
        kliky.setReps(Arrays.toString(repetitions));
        kliky.setSum(sum);
        kliky.setMax(max);

        long Id = databaseHelper.addRecord(kliky);

    }

    private String removeLastComma(String repsToSave) {
        if (repsToSave.endsWith(",")) {
            return repsToSave.substring(0, repsToSave.length() - 1);
        } else {
            return repsToSave;
        }
    }

}

