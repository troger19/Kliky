package com.itible.exercise.entity;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

/**
 * Entity holding the record for max and sum
 */
public class Statistics implements Serializable {

    @Exclude
    private String key;
    public int maxSum;
    public int maxReps;
    public String name;

    public Statistics() {
    }

    public Statistics(int maxSum, int maxReps, String name) {
        this.maxSum = maxSum;
        this.maxReps = maxReps;
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}