package com.itible.exercise;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
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
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

//TODO turn off the joke receiver from the properties
// Save the last joke number in props

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private final static String LOG_TAG = MainActivity.class.getSimpleName();
    TextView txtMaxReps, txtMaxSum, txtDayOfYear, txtExerciseName, txtAllTrainingsCount, txtAllExerciseTypes;
    Statistics statistics;
    List<Long> trainingDatesAsTime = new ArrayList<>();
    private View contentMainView;
    private StatisticsDao statisticsDao;
    private ExerciseDao exerciseDao;
    private String exerciseName, user;
    private SharedPreferences sharedPref;
    private static FirebaseDatabase firebaseDatabase;
    private final SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            exerciseName = sharedPref.getString(MyPreferencesActivity.EXERCISE_NAME_PREF, "kliky");
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);
        }
    };

    public static void cancelNotification(Context context) {
        Intent intent = new Intent(context, JokesReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pending);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (firebaseDatabase == null) {
            firebaseDatabase = FirebaseDatabase.getInstance();
//            firebaseDatabase.setPersistenceEnabled(true);
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.registerOnSharedPreferenceChangeListener(listener);
        user = sharedPref.getString(MyPreferencesActivity.USER_PREF, "jano");
        exerciseName = sharedPref.getString(MyPreferencesActivity.EXERCISE_NAME_PREF, "kliky");
        int pushUp = R.drawable.custom_push_up;

        statisticsDao = new StatisticsDao(firebaseDatabase);
        exerciseDao = new ExerciseDao(firebaseDatabase);

        txtMaxReps = findViewById(R.id.txtMaxReps);
        txtMaxSum = findViewById(R.id.txtMaxSum);
        txtDayOfYear = findViewById(R.id.txtDayOfYear);
        txtExerciseName = findViewById(R.id.txtExerciseName);
        txtAllTrainingsCount = findViewById(R.id.txtAllTrainingsCount);
        txtAllExerciseTypes = findViewById(R.id.txtAllExerciseTypes);
        contentMainView = findViewById(R.id.content_main);

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
        scheduleNotification();
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
                    refreshStatsSelectedExercise(statistics); // calling a function with data
                }
                refreshStatsSelectedExercise(statistics);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        exerciseDao.getByName(exerciseName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long childrenCount = snapshot.getChildrenCount();
                refreshStatsSelectedExerciseCount(childrenCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        exerciseDao.getByName(exerciseName).addListenerForSingleValueEvent(new ValueEventListener() {
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

        exerciseDao.getAllByUsername(user).get().addOnCompleteListener(new OnCompleteListener<>() {
            long allExercisesForUserCount;

            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult() != null) {
                        allExercisesForUserCount = task.getResult().getChildrenCount();
                        Iterable<DataSnapshot> children = task.getResult().getChildren();
                        Set<String> uniqueExerciseNames = StreamSupport.stream(children.spliterator(), false)
                                .map(a -> a.getValue(Exercise.class).getName())
                                .map(x -> x.replace(user + "_", ""))
                                .collect(Collectors.toSet());
                        refreshStatsAllTrainingsCount(uniqueExerciseNames, allExercisesForUserCount);
                    } else {
                        allExercisesForUserCount = 0;
                        Log.d(LOG_TAG, "No such user");
                    }
                } else {
                    Log.d(LOG_TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    /**
     * Load the max rep and sum on the main page
     *
     * @param statistics statistics
     */
    private void refreshStatsSelectedExercise(Statistics statistics) {
        txtMaxReps.setText(statistics == null ? "0" : "" + statistics.getMaxReps());
        txtMaxSum.setText(statistics == null ? "0" : "" + statistics.getMaxSum());
        txtExerciseName.setText(exerciseName);
    }

    /**
     * Load the current exercise count vs day of the year on the main page
     *
     * @param count number of exercise
     */
    private void refreshStatsSelectedExerciseCount(long count) {
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

    /**
     * Refresh other statistics on the main page
     *
     * @param count number of exercise
     */
    private void refreshStatsAllTrainingsCount(Set<String> uniqueExerciseNames, long count) {
        txtAllExerciseTypes.setText(uniqueExerciseNames.toString());
        Calendar calendar = Calendar.getInstance();
        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        String dayOfYear_Trainings = count + "/" + dayOfYear;
        txtAllTrainingsCount.setText(dayOfYear_Trainings);
        if (count > dayOfYear) {
            txtAllTrainingsCount.setTextColor(getResources().getColor(R.color.colorCounterPositive));
        } else {
            txtAllTrainingsCount.setTextColor(getResources().getColor(R.color.colorCounterNegative));
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
        contentMainView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        cancelNotification(getApplicationContext());
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent i = new Intent(this, MyPreferencesActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Asynchronously fill List of training dates
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
                    .replace(R.id.content_frame, calendarFragment)
                    .addToBackStack(null)
                    .commit();
            contentMainView.setVisibility(View.INVISIBLE); // hide the content on  main page, because its overlay
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

    /**
     * Notification with everyday Joke
     */
    private void scheduleNotification() {
        Intent notificationIntent = new Intent(this, JokesReceiver.class);
        notificationIntent.putExtra(JokesReceiver.NOTIFICATION_ID, 1);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 19); // For 1 PM or 2 PM
        calendar.set(Calendar.MINUTE, 13);
        calendar.set(Calendar.SECOND, 20);

//        alarmManager.set(AlarmManager. ELAPSED_REALTIME_WAKEUP , 15 * 60*60 * 1000 , pendingIntent);
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 15 * 1000, pendingIntent);
    }

    /**
     * Sharing firebase instance between multiple activities, because of OFFLINE mode and multiple DAOs
     *
     * @return FirebaseDatabase
     */
    public static FirebaseDatabase getFirebaseDB() {
        return firebaseDatabase;
    }
}
