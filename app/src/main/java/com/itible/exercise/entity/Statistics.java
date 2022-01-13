package com.itible.exercise.entity;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class Statistics implements Serializable {


    @Exclude
    private String key;
    public int maxSum;
    public int maxReps;

    public Statistics() {
    }

    public Statistics(int maxSum, int maxReps) {
        this.maxSum = maxSum;
        this.maxReps = maxReps;
    }

    public int getMaxSum() {
        return maxSum;
    }

    public void setMaxSum(int maxSum) {
        this.maxSum = maxSum;
    }

    public int getMaxReps() {
        return maxReps;
    }

    public void setMaxReps(int maxReps) {
        this.maxReps = maxReps;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}