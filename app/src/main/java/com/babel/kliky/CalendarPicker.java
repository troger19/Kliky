package com.babel.kliky;

/**
 * Created by Kaktus on 27.12.2017.
 */

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.timessquare.CalendarPickerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarPicker extends Fragment {
    private final static String LOG_TAG = MainActivity.class.getSimpleName();
    private CalendarPickerView calendar;

    public CalendarPicker() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calendar_fragment, container, false);

        //  ------  define the calendar --------
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 2);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy");
        String startDateDate = "01-07-2015"; // TODO posunut starting date
        Date startDate = null;
        try {
            startDate = sdf.parse(startDateDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        calendar = (CalendarPickerView) view.findViewById(R.id.calendar_view);
        Date today = new Date();
        calendar.init(startDate, endDate.getTime())
                .withSelectedDate(today)
                .inMode(CalendarPickerView.SelectionMode.RANGE);

        // TODO dorobit farbicky
        calendar.setCacheColorHint(Color.RED);
        calendar.setDrawingCacheBackgroundColor(Color.BLUE);
        calendar.setDrawingCacheEnabled(true);
        calendar.highlightDates(getTrainingDates());

        calendar.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                Date selectedDate = calendar.getSelectedDate();
                List<Date> selectedDates = calendar.getSelectedDates();
                Log.d(LOG_TAG, "selectedDate :  " + selectedDate);
                Log.d(LOG_TAG, "selectedDates :  " + selectedDates);
            }

            @Override
            public void onDateUnselected(Date date) {
                Date selectedDate = calendar.getSelectedDate();
                List<Date> selectedDates = calendar.getSelectedDates();
                Log.d(LOG_TAG, "selectedDate :  " + selectedDate);
                Log.d(LOG_TAG, "selectedDates :  " + selectedDates);
            }
        });

        // ------------- end of calendar configuration -------------------------

        return view;
    }

    private ArrayList<Date> getTrainingDates() {
        DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());
        return databaseHelper.getAllTrainingDays();
    }

}


