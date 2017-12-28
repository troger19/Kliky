package com.babel.kliky;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.babel.kliky.util.CSVWriter;
import com.babel.kliky.util.DatabaseHelper;

import java.io.File;
import java.io.FileWriter;
import java.util.Calendar;

public class MyPreferencesActivity extends PreferenceActivity {

    public static final String NUMBER_OF_SERIES_PREF = "number_of_series_preference";
    public static final String PAUSE_PREF = "pause_preference";
    public static final String MOBILE_NUMBER_PREF = "mobile_number_preference";
    final static String DROP_DATABASE_PREF = "drop_db";
    final static String EXPORT_DATABASE_PREF = "export_db";
    final static String SENDING_SMS = "alarm_preference";
    private final static String LOG_TAG = MyPreferencesActivity.class.getSimpleName();
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
        databaseHelper = new DatabaseHelper(this);

        final String action = getIntent().getAction();
        if (action != null && action.equals(DROP_DATABASE_PREF)) {
            showDeleteDatabaseDialog();
        }

        if (action != null && action.equals(EXPORT_DATABASE_PREF)) {
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);
            String todaysDate = mYear + "_" + (mMonth + 1) + "_" + mDay;
            String fileName = "kliky_" + todaysDate + ".csv";
            exportDB(fileName);
            Toast.makeText(MyPreferencesActivity.this, "Exported to jano/" + fileName, Toast.LENGTH_LONG).show();
        }
    }

    private void showDeleteDatabaseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure you want to delete all trainings?");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(LOG_TAG, "Droping database");
                getApplication().deleteDatabase(DatabaseHelper.DATABASE_NAME);

                Intent intent = new Intent(MyPreferencesActivity.this, MainActivity.class);
                overridePendingTransition(0, 0);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                overridePendingTransition(0, 0);
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

    private void exportDB(String fileName) {
        File exportDir = new File(Environment.getExternalStorageDirectory() + "/jano", "");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        File exportFile = new File(exportDir, "/" + fileName);
        try {
            exportFile.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(exportFile));
            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM kliky", null);
            csvWrite.writeNext(cursor.getColumnNames());
            while (cursor.moveToNext()) {
                String columns[] = {
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_REPS)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_SUM)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_MAX))
                };
                csvWrite.writeNext(columns);
            }
            csvWrite.close();
            cursor.close();
        } catch (Exception sqlEx) {
            Log.e(LOG_TAG, sqlEx.getMessage(), sqlEx);
        }
    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference);
        }
    }
}