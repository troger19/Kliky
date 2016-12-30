package com.babel.kliky;

import java.util.Comparator;

/**
 * Created by jan.babel on 28/12/2016.
 */


class MaxRepsComparator implements Comparator<Kliky> {

    @Override
    public int compare(Kliky kliky1, Kliky kliky2) {
        Integer sum1 = kliky1.getSum();
        Integer sum2 = kliky2.getSum();
        return sum1.compareTo(sum2);
    }

}
