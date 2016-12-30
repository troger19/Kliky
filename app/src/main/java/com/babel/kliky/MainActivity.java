package com.babel.kliky;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import android.Manifest;


public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private final static String LOG_TAG = MainActivity.class.getSimpleName();
    public final static String BALANCE = "balance";
    public final static String MAX = "max";
    public final static String SUM = "sum";
    private FloatingActionButton fab;
    public static final String DATABASE_NAME = "kliky.db";
    CustomAdapterKliky adapter;
    ArrayList<Kliky> listItems;
    DatabaseHelper databaseHelper;
    ListView listView;
    TextView txtMaxReps, txtMaxSum, txtDayOfYear;
    private int deleteItem;
    private PendingIntent pendingIntent;
    private Intent alarmIntent;
    AlarmManager alarmManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Calendar calendar = Calendar.getInstance();
        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean shouldSendSms = sharedPref.getBoolean(MyPreferencesActivity.SENDING_SMS, false);

        int arrow_down = R.drawable.custom_arrow_down;
        int arrow_up = R.drawable.custom_arrow_up;
        int pushUp = R.drawable.custom_push_up;

        // We're getting our listView by the id
        listView = (ListView) findViewById(R.id.list);
        txtMaxReps = (TextView) findViewById(R.id.txtMaxReps);
        txtMaxSum = (TextView) findViewById(R.id.txtMaxSum);
        txtDayOfYear = (TextView) findViewById(R.id.txtDayOfYear);


        databaseHelper = new DatabaseHelper(this);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(pushUp);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CounterActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        listItems = databaseHelper.getAllNotes();

        Collections.sort(listItems, new Comparator<Kliky>() {
            @Override
            public int compare(Kliky kliky1, Kliky kliky2) {
                return kliky1.getDate().compareTo(kliky2.getDate());
            }
        });


        Kliky maxRepKlik;
        Kliky maxSumKlik;
        if (listItems.isEmpty()) {
            maxRepKlik = new Kliky(null, "", 0, 0);
            maxSumKlik = new Kliky(null, "", 0, 0);
        } else {
            maxRepKlik = Collections.max(listItems, new MaxRepsComparator());
            maxSumKlik = Collections.max(listItems, new MaxSumComparator());
        }

        int maxKlik = maxRepKlik.getMax();
        txtMaxReps.setText(String.valueOf(maxKlik));
        int maxSum = maxSumKlik.getSum();
        txtMaxSum.setText(String.valueOf(maxSum));
        String dayOfYear_Trainings = listItems.size() + "/" + Integer.toString(dayOfYear);
        txtDayOfYear.setText(dayOfYear_Trainings);
        if (listItems.size() > dayOfYear) {
            txtDayOfYear.setTextColor(getResources().getColor(R.color.colorCounterPositive));
        } else {
            txtDayOfYear.setTextColor(getResources().getColor(R.color.colorCounterNegative));
        }

        adapter = new CustomAdapterKliky(listItems, getApplicationContext(), maxKlik, maxSum, arrow_down, arrow_up);
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(myClickListener);

        alarmIntent = new Intent(MainActivity.this, AlarmReceiver.class);
        alarmIntent.putExtra(BALANCE, dayOfYear_Trainings);
        alarmIntent.putExtra(MAX, maxKlik);
        alarmIntent.putExtra(SUM, maxSum);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, alarmIntent, 0);
        // cancel the intent so the Alert Manager running on Background is deactivated
        if (!shouldSendSms) {
            cancelAlarm();
        } else {
            startAt10();
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    private void exportDB() {
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source = null;
        FileChannel destination = null;
        String currentDBPath = "/data/" + "com.babel.kliky" + "/databases/" + DATABASE_NAME;
        String backupDBPath = DATABASE_NAME;
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            Toast.makeText(MainActivity.this, "DB Exported!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, MyPreferencesActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_camera) {
            Log.d(LOG_TAG, "Camera pressed ");
            Toast.makeText(MainActivity.this, "Camera pressed", Toast.LENGTH_LONG).show();
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            Log.d(LOG_TAG, "Gallery pressed ");
            Toast.makeText(MainActivity.this, "Gallery pressed", Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_slideshow) {
            Log.d(LOG_TAG, "Slideshow pressed ");
            Toast.makeText(MainActivity.this, "Simka lubi Janka", Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_manage) {
            Log.d(LOG_TAG, "Manager pressed ");
            Toast.makeText(MainActivity.this, "Janko lubi Simonku", Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_share) {
            Log.d(LOG_TAG, "Navshare pressed ");
            Toast.makeText(MainActivity.this, "S+J=VL", Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_send) {
            Log.d(LOG_TAG, "Navsend pressed ");
            Toast.makeText(MainActivity.this, "Zabaci KVA-KVAK", Toast.LENGTH_LONG).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     * On a long click delete the selected item
     */
    public AdapterView.OnItemLongClickListener myClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
            Log.d(LOG_TAG, "Long click pressed");
            deleteItem = position;

            AlertDialog alert = new AlertDialog.Builder(parent.getContext())
                    .setTitle("Delete " + listItems.get(position).date)
                    .setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    Kliky kliky = listItems.get(deleteItem);
                                    databaseHelper.deleteKliky(kliky.id);

                                    listItems.remove(deleteItem);
                                    adapter.notifyDataSetChanged();

                                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                    overridePendingTransition(0, 0);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    finish();
                                    overridePendingTransition(0, 0);
                                    startActivity(intent);
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    dialog.cancel();
                                }
                            }).show();

            return false;
        }


    };

    public void startAt10() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        /* Set the alarm to start at 10:30 AM */
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 19);
        calendar.set(Calendar.MINUTE, 0);

        /* Repeating on every 2 minutes interval */
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
//                1000 * 60 *2, pendingIntent);


    }

    public void cancelAlarm() {
        // If the alarm has been set, cancel it.
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }

    }

}
