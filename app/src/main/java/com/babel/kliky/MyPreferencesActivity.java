package com.babel.kliky;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class MyPreferencesActivity extends PreferenceActivity {

    public static final String NUMBER_OF_SERIES_PREF = "number_of_series_preference";
    public static final String PAUSE_PREF = "pause_preference";
    public static final String MOBILE_NUMBER_PREF = "mobile_number_preference";
    public static final String SMS_MESSAGE_PREF = "sms_message_preference";
    public static final String DATABASE_NAME = "kliky.db";
    final static String DROP_DATABASE_PREF = "drop_db";
    final static String EXPORT_DATABASE_PREF = "export_db";
    final static String SENDING_SMS = "alarm_preference";
    private final static String LOG_TAG = MyPreferencesActivity.class.getSimpleName();
    private static final Pattern DIR_SEPORATOR = Pattern.compile("/");
    private DatabaseHelper databaseHelper;

    private static String[] getStorageDirectories() {
        // Final set of paths
        final Set<String> rv = new HashSet<String>();
        // Primary physical SD-CARD (not emulated)
        final String rawExternalStorage = System.getenv("EXTERNAL_STORAGE");
        // All Secondary SD-CARDs (all exclude primary) separated by ":"
        final String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
        // Primary emulated SD-CARD
        final String rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET");
        if (TextUtils.isEmpty(rawEmulatedStorageTarget)) {
            // Device has physical external storage; use plain paths.
            if (TextUtils.isEmpty(rawExternalStorage)) {
                // EXTERNAL_STORAGE undefined; falling back to default.
                rv.add("/storage/sdcard0");
            } else {
                rv.add(rawExternalStorage);
            }
        } else {
            // Device has emulated storage; external storage paths should have
            // userId burned into them.
            final String rawUserId;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                rawUserId = "";
            } else {
                final String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                final String[] folders = DIR_SEPORATOR.split(path);
                final String lastFolder = folders[folders.length - 1];
                boolean isDigit = false;
                try {
                    Integer.valueOf(lastFolder);
                    isDigit = true;
                } catch (NumberFormatException ignored) {
                }
                rawUserId = isDigit ? lastFolder : "";
            }
            // /storage/emulated/0[1,2,...]
            if (TextUtils.isEmpty(rawUserId)) {
                rv.add(rawEmulatedStorageTarget);
            } else {
                rv.add(rawEmulatedStorageTarget + File.separator + rawUserId);
            }
        }
        // Add all secondary storages
        if (!TextUtils.isEmpty(rawSecondaryStoragesStr)) {
            // All Secondary SD-CARDs splited into array
            final String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator);
            Collections.addAll(rv, rawSecondaryStorages);
        }
        return rv.toArray(new String[rv.size()]);
    }

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
//            exportDB();
            exportDB1();
            Toast.makeText(MyPreferencesActivity.this, "Export is not working!", Toast.LENGTH_LONG).show();
            ArrayList<Kliky> allNotes = databaseHelper.getAllNotes();
            Log.d(LOG_TAG, String.valueOf(allNotes.size()));


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

//    private void exportDB() {
//        String[] storageDirectories = getStorageDirectories();
////   /sdcard
////                /storage/sdcard1
////        File sd = Environment.getExternalStorageDirectory();
////        File sd = new File("/sdcard");
//        File sd = new File("/sdcard/jano/jano.db");
//        File sd1 = new File("/storage/sdcard1/jano/jano.db");
//        File sd2 = new File("/jano.db");
//        String dir = Environment.getExternalStorageDirectory().getAbsolutePath();
//        Log.d(LOG_TAG, sd + "  " + dir);
//        File data = Environment.getDataDirectory();
//        FileChannel source = null;
//        FileChannel destination = null;
//        String currentDBPath = "/data/" + "com.babel.kliky" + "/databases/" + DATABASE_NAME;
//        String backupDBPath = DATABASE_NAME;
//        File currentDB = new File(data, currentDBPath);
////        File backupDB = new File(sd, backupDBPath);
//        File backupDB = new File("/data/jano/", "yourFolder");
//        try {
//            source = new FileInputStream(currentDB).getChannel();
//
//
//            destination = new FileOutputStream(backupDB).getChannel();
//            destination.transferFrom(source, 0, source.size());
//            source.close();
//            destination.close();
//            Toast.makeText(MyPreferencesActivity.this, "DB Exported!", Toast.LENGTH_LONG).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


    private void exportDB1() {
//        verifyStoragePermissions(this);
//        java.io.File xmlFile = new java.io.File(Environment
//                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//                + "/Filename.xml");

        File dbFile = getDatabasePath("KlikyDb.db");
//        DBHelper dbhelper = new DBHelper(getApplicationContext());
        File exportDir = new File(Environment.getExternalStorageDirectory() + "/jano", "");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }

        File file2 = new File(exportDir, "/csvname2.csv");
        try {
//            xmlFile.createNewFile();
            file2.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file2));
//            SQLiteDatabase db = dbhelper.getReadableDatabase();
            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            Cursor curCSV = db.rawQuery("SELECT * FROM kliky", null);
            csvWrite.writeNext(curCSV.getColumnNames());
            while (curCSV.moveToNext()) {
                //Which column you want to exprort
                String arrStr[] = {curCSV.getString(0), curCSV.getString(1), curCSV.getString(2)};
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
        } catch (Exception sqlEx) {
            Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
        }
    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference);
        }
    }

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
//    public static void verifyStoragePermissions(Activity activity) {
//        // Check if we have write permission
//        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission_group.STORAGE);
//
//        if (permission != PackageManager.PERMISSION_GRANTED) {
//            // We don't have permission so prompt the user
//            ActivityCompat.requestPermissions(
//                    activity,
//                    PERMISSIONS_STORAGE,
//                    REQUEST_EXTERNAL_STORAGE
//            );
//        }
//    }

}