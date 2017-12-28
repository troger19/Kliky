package com.babel.kliky;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.babel.kliky.comparator.MaxRepsComparator;
import com.babel.kliky.comparator.MaxSumComparator;
import com.babel.kliky.entity.Kliky;
import com.babel.kliky.fragments.CalendarPicker;
import com.babel.kliky.fragments.CounterActivity;
import com.babel.kliky.util.DatabaseHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public final static String BALANCE = "balance";
    public final static String MAX = "max";
    public final static String SUM = "sum";
    private final static String LOG_TAG = MainActivity.class.getSimpleName();
    CustomAdapterKliky adapter;
    ArrayList<Kliky> listItems;
    DatabaseHelper databaseHelper;
    ListView listView;
    TextView txtMaxReps, txtMaxSum, txtDayOfYear;
    AlarmManager alarmManager;
    String pickedUpdateDate;
    private FloatingActionButton fab;
    private int deleteItem;

    /**
     * On a long click delete the selected item, or update a date of a training
     */
    public AdapterView.OnItemLongClickListener myClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(final AdapterView<?> parent, View v, int position, long id) {
            deleteItem = position;

            new AlertDialog.Builder(parent.getContext())
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
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    finish();
                                    overridePendingTransition(0, 0);
                                    startActivity(intent);
                                }
                            })
                    .setNeutralButton("Pick a Date", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final int mYear, mMonth, mDay;
                            final Calendar c = Calendar.getInstance();
                            mYear = c.get(Calendar.YEAR);
                            mMonth = c.get(Calendar.MONTH);
                            mDay = c.get(Calendar.DAY_OF_MONTH);

                            final DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    pickedUpdateDate = dayOfMonth + "." + (monthOfYear + 1) + "." + year;
                                }
                            };
                            final DatePickerDialog datePickerDialog = new DatePickerDialog(parent.getContext(),
                                    datePickerListener, mYear, mMonth, mDay);

                            datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            DatePicker datePicker = datePickerDialog.getDatePicker();
                                            datePickerListener.onDateSet(datePicker, datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                                            Toast.makeText(MainActivity.this, "Update a date " + pickedUpdateDate, Toast.LENGTH_SHORT).show();
                                            Kliky kliky = listItems.get(deleteItem);
                                            kliky.setDate(DatabaseHelper.convertStringToDate(pickedUpdateDate));
                                            databaseHelper.updateRecord(kliky);
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                            datePickerDialog.show();
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
    private PendingIntent pendingIntent;
    private Intent alarmIntent;

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

        alarmIntent = new Intent(MainActivity.this, JokesReceiver.class);
        alarmIntent.putExtra(BALANCE, dayOfYear_Trainings);
        alarmIntent.putExtra(MAX, maxKlik);
        alarmIntent.putExtra(SUM, maxSum);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, alarmIntent, 0);
        // cancel the intent so the Alert Manager running on Background is deactivated
        if (!shouldSendSms) {
            cancelAlarm();
        } else {
            smsAlarmSetup();
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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_calendar) { // display calendar
            CalendarPicker calendarFragment = new CalendarPicker();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_main, calendarFragment)
                    .addToBackStack(null)
                    .commit();
        } else if (id == R.id.nav_trainings) { // display training list
            Intent mainActivityIntent = new Intent(MainActivity.this, MainActivity.class);
            overridePendingTransition(0, 0);
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            finish();
            overridePendingTransition(0, 0);
            startActivity(mainActivityIntent);
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

    public void smsAlarmSetup() {
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
