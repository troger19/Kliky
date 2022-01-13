package com.itible.exercise.entity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;

public class ExerciseDao {
    private final DatabaseReference databaseReference;

    public ExerciseDao() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
//        db.setPersistenceEnabled(true);
        databaseReference = db.getReference(Exercise.class.getSimpleName());
    }

    public Task<Void> add(Exercise emp) {
        return databaseReference.push().setValue(emp);
    }

    public Task<Void> update(String key, HashMap<String, Object> hashMap) {
        return databaseReference.child(key).updateChildren(hashMap);
    }

    public Task<Void> remove(String key) {
        return databaseReference.child(key).removeValue();
    }

    public Query get(String key) {
        if (key == null) {
//            return databaseReference.orderByKey().limitToFirst(8);
//            return databaseReference.orderByKey();
            return databaseReference.orderByChild("date");
        }
//        return databaseReference.orderByKey().startAfter(key).limitToFirst(8);
//        return databaseReference.orderByChild("date").orderByChild("time");
        return databaseReference.orderByChild("date");
    }

    public Query getAll() {
        return databaseReference.orderByKey();
    }
}