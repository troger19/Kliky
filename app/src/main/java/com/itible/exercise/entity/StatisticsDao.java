package com.itible.exercise.entity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;

public class StatisticsDao {
    private final DatabaseReference databaseReference;

    public StatisticsDao() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
//        db.setPersistenceEnabled(true);
        databaseReference = db.getReference(Statistics.class.getSimpleName());
    }

    public Task<Void> add(Statistics emp) {
        return databaseReference.push().setValue(emp);
    }

    public Task<Void> update(String key, HashMap<String, Object> hashMap) {
        return databaseReference.child(key).updateChildren(hashMap);
    }

    public Task<Void> remove(String key) {
        return databaseReference.child(key).removeValue();
    }

    public Query get(String name) {
        return databaseReference.orderByChild("name").equalTo(name).limitToFirst(1);
    }
}