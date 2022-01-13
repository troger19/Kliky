package com.itible.exercise;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.itible.exercise.entity.Exercise;
import com.itible.exercise.entity.ExerciseDao;
import com.itible.exercise.entity.Statistics;
import com.itible.exercise.entity.StatisticsDao;
import com.itible.exercise.fragments.CalendarPicker;
import com.itible.exercise.fragments.CounterActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public final static String BALANCE = "balance";
    public final static String MAX = "max";
    public final static String SUM = "sum";
    private final static String LOG_TAG = MainActivity.class.getSimpleName();
    ListView listView;
    TextView txtMaxReps, txtMaxSum, txtDayOfYear;
    Statistics statistics;
    List<Long> trainingDatesAsTime = new ArrayList<>();
    private StatisticsDao statisticsDao;
    private ExerciseDao exerciseDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        int pushUp = R.drawable.custom_push_up;

        statisticsDao = new StatisticsDao();
        exerciseDao = new ExerciseDao();

        listView = findViewById(R.id.list);
        txtMaxReps = findViewById(R.id.txtMaxReps);
        txtMaxSum = findViewById(R.id.txtMaxSum);
        txtDayOfYear = findViewById(R.id.txtDayOfYear);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setImageResource(pushUp);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, CounterActivity.class);
            intent.putExtra("Statistics", statistics);
            startActivity(intent);
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        loadData();
//
//        listItems = new ArrayList<>();
//
//        dao.get(null).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
////                List<Exercise> tmpList=  new ArrayList<>();
//                for (DataSnapshot data : snapshot.getChildren()) {
//                    Exercise exercise = data.getValue(Exercise.class);
//                    listItems.add(exercise);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//        Collections.sort(listItems, new Comparator<Exercise>() {
//            @Override
//            public int compare(Exercise exercise1, Exercise exercise2) {
//                return exercise1.getDate().compareTo(exercise2.getDate());
//            }
//        });
//
//        Exercise maxRepKlik;
//        Exercise maxSumKlik;
//

//        if (listItems.isEmpty()) {
//            maxRepKlik = new Exercise(null, "", 0, 0,true);
//            maxSumKlik = new Exercise(null, "", 0, 0,true);
//        } else {
//            maxRepKlik = Collections.max(listItems, new MaxRepsComparator());
//            maxSumKlik = Collections.max(listItems, new MaxSumComparator());
//        }
//
//        int maxKlik = maxRepKlik.getMax();
//        txtMaxReps.setText(statistics==null?"0":""+statistics.getMaxReps());
//        int maxSum = maxSumKlik.getSum();
//        txtMaxSum.setText(statistics==null?"0":""+statistics.getMaxSum());


//        adapter = new CustomAdapterKliky(listItems, getApplicationContext(), maxKlik, maxSum, arrow_down, arrow_up);
//        listView.setAdapter(adapter);
//
//        alarmIntent = new Intent(MainActivity.this, JokesReceiver.class);
//        alarmIntent.putExtra(BALANCE, dayOfYear_Trainings);
//        alarmIntent.putExtra(MAX, maxKlik);
//        alarmIntent.putExtra(SUM, maxSum);
//        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, alarmIntent, 0);
////         cancel the intent so the Alert Manager running on Background is deactivated
//        if (!shouldSendSms) {
//            cancelAlarm();
//        } else {
//            smsAlarmSetup();
//        }
    }

    private void loadData() {
        statisticsDao.get().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    statistics = data.getValue(Statistics.class);
                    statistics.setKey(data.getKey());
                    refreshStatistics(statistics); // calling a function with data
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        exerciseDao.getAll().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long childrenCount = snapshot.getChildrenCount();
                refreshStatistics2(childrenCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        exerciseDao.getAll().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    Exercise exercise = data.getValue(Exercise.class);
                    addTrainingDates(exercise.getDate());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private void refreshStatistics(Statistics statistics) {
        txtMaxReps.setText(statistics == null ? "0" : "" + statistics.getMaxReps());
        txtMaxSum.setText(statistics == null ? "0" : "" + statistics.getMaxSum());
    }

    private void refreshStatistics2(long count) {
        Calendar calendar = Calendar.getInstance();
        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        int arrow_down = R.drawable.custom_arrow_down;
        int arrow_up = R.drawable.custom_arrow_up;
//
        String dayOfYear_Trainings = count + "/" + dayOfYear;
        txtDayOfYear.setText(dayOfYear_Trainings);
        if (count > dayOfYear) {
            txtDayOfYear.setTextColor(getResources().getColor(R.color.colorCounterPositive));
        } else {
            txtDayOfYear.setTextColor(getResources().getColor(R.color.colorCounterNegative));
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent i = new Intent(this, MyPreferencesActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Assynchornuosly fill List of training dates
     *
     * @param date time in milis
     */
    private void addTrainingDates(long date) {
        trainingDatesAsTime.add(date);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_calendar) { // display calendar
            CalendarPicker calendarFragment = new CalendarPicker(trainingDatesAsTime);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_main, calendarFragment)
                    .addToBackStack(null)
                    .commit();
        } else if (id == R.id.nav_slideshow) {
            Intent intent = new Intent(MainActivity.this, LoadTrainingActivity.class);
            startActivity(intent);
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

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
