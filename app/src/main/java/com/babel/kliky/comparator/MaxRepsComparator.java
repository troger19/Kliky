package com.babel.kliky.comparator;

import com.babel.kliky.entity.Kliky;

import java.util.Comparator;

/**
 * Created by jan.babel on 28/12/2016.
 */


public class MaxRepsComparator implements Comparator<Kliky> {

    @Override
    public int compare(Kliky kliky1, Kliky kliky2) {
        Integer sum1 = kliky1.getMax();
        Integer sum2 = kliky2.getMax();
        return sum1.compareTo(sum2);
    }

}
