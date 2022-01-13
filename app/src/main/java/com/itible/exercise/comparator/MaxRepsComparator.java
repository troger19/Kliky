package com.itible.exercise.comparator;


import com.itible.exercise.entity.Exercise;

import java.util.Comparator;

/**
 * Created by jan.babel on 28/12/2016.
 */


public class MaxRepsComparator implements Comparator<Exercise> {

    @Override
    public int compare(Exercise exercise1, Exercise exercise2) {
        Integer sum1 = exercise1.getMax();
        Integer sum2 = exercise2.getMax();
        return sum1.compareTo(sum2);
    }

}
