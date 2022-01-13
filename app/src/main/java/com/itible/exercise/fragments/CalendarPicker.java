package com.itible.exercise.fragments;

/**
 * Created by jan.babel on 27.12.2017.
 */

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.itible.exercise.R;
import com.squareup.timessquare.CalendarPickerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarPicker extends Fragment {
    private final static String LOG_TAG = CalendarPicker.class.getSimpleName();
    private final List<Long> trainingDatesAsTime;

    public CalendarPicker(List<Long> trainingDatesAsTime) {
        this.trainingDatesAsTime = trainingDatesAsTime;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calendar_fragment, container, false);
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 24);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy", Locale.ENGLISH);
        String startDateDate = "01-12-2017";
        Date startDate = null;
        try {
            startDate = sdf.parse(startDateDate);
        } catch (ParseException e) {
            Log.e(LOG_TAG, e.getMessage());
        }

        CalendarPickerView calendar = view.findViewById(R.id.calendar_view);
        Date today = new Date();
        calendar.init(startDate, endDate.getTime())
                .withSelectedDate(today)
                .inMode(CalendarPickerView.SelectionMode.RANGE);

        calendar.setCacheColorHint(Color.RED);
        calendar.highlightDates(getTrainingDates());
        return view;
    }

    /**
     * Load training dates from Firebase DB
     *
     * @return List of dates of trainings
     */
    private ArrayList<Date> getTrainingDates() {
        ArrayList<Date> trainingDates = new ArrayList<>();
        trainingDatesAsTime.forEach(e -> trainingDates.add((new Date(e))));
        return trainingDates;
    }
}


