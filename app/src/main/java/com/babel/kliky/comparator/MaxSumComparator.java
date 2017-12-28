package com.babel.kliky.comparator;

import com.babel.kliky.entity.Kliky;

import java.util.Comparator;

/**
 * Created by jan.babel on 28/12/2016.
 */


public class MaxSumComparator implements Comparator<Kliky> {

    @Override
    public int compare(Kliky kliky1, Kliky kliky2) {
        Integer max1 = kliky1.getSum();
        Integer max2 = kliky2.getSum();
        return max1.compareTo(max2);
    }

}
