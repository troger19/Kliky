package com.babel.kliky.entity;

import java.util.Date;

public class Kliky {
    public int id;
    public Date date;
    public String reps;
    public int sum;
    public int max;

    public Kliky() {
    }

    public Kliky(Date date, String reps, int sum, int max) {
        this.date = date;
        this.reps = reps;
        this.sum = sum;
        this.max = max;
    }

    public int getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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
}