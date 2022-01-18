package com.itible.exercise.util;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

public class Util {

    public static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

    public static String removeLastComma(String repsToSave) {
        if (repsToSave.endsWith(",")) {
            return repsToSave.substring(0, repsToSave.length() - 1);
        } else {
            return repsToSave;
        }
    }

    /**
     * Check if inputted values make sense.
     *
     * @param max  max reps
     * @param sum  sum of all reps in training
     * @param reps array of reps per training
     * @return true is max is the maximum value from array and sum is the sum of array
     */
    public static boolean insertedValuesCheck(String max, String sum, String reps) {
        List<String> strings = Arrays.asList(reps.replace("[", "").replace("]", "").replace(" ", "").split(","));
        int maxFromReps = strings.stream()
                .map(Integer::parseInt)
                .mapToInt(v -> v)
                .max().orElseThrow(NoSuchElementException::new);
        int sumFromReps = strings.stream()
                .map(Integer::parseInt)
                .mapToInt(v -> v)
                .sum();
        return maxFromReps == Integer.parseInt(max) && sumFromReps == Integer.parseInt(sum);
    }

}
