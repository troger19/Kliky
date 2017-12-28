package com.babel.kliky;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.babel.kliky.entity.Kliky;
import com.babel.kliky.util.DatabaseHelper;

import java.util.ArrayList;

/**
 * Created by jan.babel on 26/12/2016.
 */

public class CustomAdapterKliky extends ArrayAdapter<Kliky> {
    private final static String LOG_TAG = CustomAdapterKliky.class.getSimpleName();
    private final DatabaseHelper databaseHelper;
    private ArrayList<Kliky> dataSet;
    Context mContext;
    private int maxRep, maxSum;
    int arrowDown,arrowUp;

    // View lookup cache
    private static class ViewHolder {
        TextView txtDate;
        TextView txtReps;
        TextView txtSum;
        TextView txtMax;
        ImageView imageView;
    }

    public CustomAdapterKliky(ArrayList<Kliky> data, Context context, int maxRep, int maxSum, int arrowDown, int arrowUp) {
        super(context, R.layout.custom_kliky, data);
        this.dataSet = data;
        this.mContext = context;
        this.maxRep = maxRep;
        this.maxSum = maxSum;
        this.arrowDown= arrowDown;
        this.arrowUp= arrowUp;

        databaseHelper = new DatabaseHelper(getContext());


    }

    public void onClick(View v) {
        Log.i(LOG_TAG, "onClick111 Pressed");
        int position = (Integer) v.getTag();
        Object object = getItem(position);
        Kliky dataModel = (Kliky) object;

        switch (v.getId()) {
            case R.id.kliky_date:
                Snackbar.make(v, "Release txtDate " + dataModel.getDate(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
                break;
        }
    }


    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        Log.i(LOG_TAG, "getView111 Pressed");
        // Get the data item for this position
        final Kliky dataModel = getItem(position);
        Kliky previousDataModel = null;
        if (position > 0) {
            previousDataModel = getItem(position - 1);
        }

        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.custom_kliky, parent, false);
            viewHolder.txtDate = (TextView) convertView.findViewById(R.id.kliky_date);
            viewHolder.txtReps = (TextView) convertView.findViewById(R.id.kliky_reps);
            viewHolder.txtSum = (TextView) convertView.findViewById(R.id.kliky_sum);
            viewHolder.txtMax = (TextView) convertView.findViewById(R.id.kliky_max);

            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.icon22);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txtDate.setText(DatabaseHelper.convertDateToString(dataModel.getDate()));
        viewHolder.txtReps.setText(dataModel.getReps());
        viewHolder.txtSum.setText(String.valueOf(dataModel.getSum()));
        viewHolder.txtMax.setText(String.valueOf(dataModel.getMax()));

        if (position > 0 && Integer.valueOf(viewHolder.txtMax.getText().toString()) > previousDataModel.getMax()) {
            viewHolder.imageView.setImageDrawable(mContext.getResources().getDrawable(arrowUp));
        } else {
            viewHolder.imageView.setImageDrawable(mContext.getResources().getDrawable(arrowDown));
        }
        if (Integer.valueOf(viewHolder.txtMax.getText().toString()) == maxRep) {
            viewHolder.txtMax.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
        }

        // Return the completed view to render on screen
        return convertView;
    }



}