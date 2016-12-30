package com.babel.kliky;

import java.util.Comparator;

/**
 * Created by jan.babel on 28/12/2016.
 */


class MaxSumComparator implements Comparator<Kliky> {

    @Override
    public int compare(Kliky kliky1, Kliky kliky2) {
        Integer max1 = kliky1.getMax();
        Integer max2 = kliky2.getMax();
        return max1.compareTo(max2);
    }

}
