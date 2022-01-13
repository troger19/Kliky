package com.itible.exercise.comparator;

import com.itible.exercise.entity.Exercise;

import java.util.Comparator;

/**
 * Created by jan.babel on 28/12/2016.
 */


public class MaxSumComparator implements Comparator<Exercise> {

    @Override
    public int compare(Exercise exercise1, Exercise exercise2) {
        Integer max1 = exercise1.getSum();
        Integer max2 = exercise2.getSum();
        return max1.compareTo(max2);
    }

}
