package com.itible.exercise.entity;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class Exercise implements Serializable {

    @Exclude
    private String key;
    public long date;
    public String reps;
    public int sum;
    public int max;
    public boolean progress;

    public Exercise() {
    }

    public Exercise(long date, String reps, int sum, int max, boolean progress) {
        this.date = date;
        this.reps = reps;
        this.sum = sum;
        this.max = max;
        this.progress = progress;
    }

    public String getReps() {
        return reps;
    }

    public void setReps(String reps) {
        this.reps = reps;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isProgress() {
        return progress;
    }

    public void setProgress(boolean progress) {
        this.progress = progress;
    }
}