package com.itible.exercise;

import android.os.Bundle;

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

import java.util.ArrayList;

public class LoadTrainingActivity extends AppCompatActivity {
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    RVAdapter adapter;
    ExerciseDao exerciseDao;
    StatisticsDao statsDao;
    boolean isLoading = false;
    String key = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_training);

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
        statsDao = new StatisticsDao();
        adapter = new RVAdapter(this, arrow_up, arrow_down, exerciseDao);
        recyclerView.setAdapter(adapter);
        loadData();
    }

    private void loadData() {
        swipeRefreshLayout.setRefreshing(true);
        exerciseDao.get(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Exercise> emps = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Exercise emp = data.getValue(Exercise.class);
                    emp.setKey(data.getKey());
                    emps.add(emp);
                    key = data.getKey();
                }
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