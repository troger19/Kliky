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

    public Query getByUser(String exerciseName) {
        return databaseReference.orderByChild("name").equalTo(exerciseName);
    }


    public Query getAll(String exerciseName) {
        return databaseReference.orderByChild("name").equalTo(exerciseName);
    }
}