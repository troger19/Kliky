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
        String user = sharedPref.getString(MyPreferencesActivity.USER_PREF, "jano");
        String exerciseName = sharedPref.getString(MyPreferencesActivity.EXERCISE_NAME_PREF, "kliky");

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
        loadData(user + "_" + exerciseName);
    }

    /**
     * Load saved exercises and statistics
     *
     * @param exerciseName exercise name containing user and exercise name
     */
    private void loadData(String exerciseName) {
        statisticsDao.get(exerciseName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    statistics = data.getValue(Statistics.class);
                    statistics.setKey(data.getKey());
                    refreshStatisticsText(statistics); // calling a function with data
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        exerciseDao.getAll(exerciseName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long childrenCount = snapshot.getChildrenCount();
                refreshStatisticsText2(childrenCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        exerciseDao.getAll(exerciseName).addListenerForSingleValueEvent(new ValueEventListener() {
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

    /**
     * Load the max rep and sum on the main page
     *
     * @param statistics statistics
     */
    private void refreshStatisticsText(Statistics statistics) {
        txtMaxReps.setText(statistics == null ? "0" : "" + statistics.getMaxReps());
        txtMaxSum.setText(statistics == null ? "0" : "" + statistics.getMaxSum());
    }

    /**
     * Refresh other statistics on the main page
     *
     * @param count number of exercise
     */
    private void refreshStatisticsText2(long count) {
        Calendar calendar = Calendar.getInstance();
        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
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
