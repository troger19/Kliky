package com.itible.exercise.util;

import com.itible.exercise.entity.Exercise;

import java.util.Comparator;

public class DateComparator implements Comparator<Exercise> {

    @Override
    public int compare(Exercise exercise1, Exercise exercise2) {
        Long max1 = exercise1.getDate();
        Long max2 = exercise2.getDate();
        return max1.compareTo(max2);
    }

}
