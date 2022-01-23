package com.itible.exercise;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.itible.exercise.entity.Exercise;
import com.itible.exercise.entity.ExerciseDao;
import com.itible.exercise.entity.StatisticsDao;
import com.itible.exercise.util.DateComparator;

import java.util.ArrayList;

public class LoadTrainingActivity extends AppCompatActivity {
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    RVAdapter adapter;
    ExerciseDao exerciseDao;
    StatisticsDao statisticsDao;
    boolean isLoading = false;
    String key = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_training);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String user = sharedPref.getString(MyPreferencesActivity.USER_PREF, "jano");
        String exerciseName = sharedPref.getString(MyPreferencesActivity.EXERCISE_NAME_PREF, "kliky");

        swipeRefreshLayout = findViewById(R.id.swip);
        recyclerView = findViewById(R.id.rv);
        recyclerView = findViewById(R.id.rv);
        recyclerView = findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        int arrow_down = R.drawable.custom_arrow_down;
        int arrow_up = R.drawable.custom_arrow_up;
        exerciseDao = new ExerciseDao();
        statisticsDao = new StatisticsDao();
        adapter = new RVAdapter(this, arrow_up, arrow_down, exerciseDao, statisticsDao);
        recyclerView.setAdapter(adapter);
        loadData(user + "_" + exerciseName);
    }

    private void loadData(String exerciseName) {
        swipeRefreshLayout.setRefreshing(true);
        exerciseDao.getByUser(exerciseName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Exercise> emps = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Exercise emp = data.getValue(Exercise.class);
                    emp.setKey(data.getKey());
                    emps.add(emp);
                    key = data.getKey();
                }
                emps.sort(new DateComparator());
                adapter.setItems(emps);
                adapter.notifyDataSetChanged();
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}