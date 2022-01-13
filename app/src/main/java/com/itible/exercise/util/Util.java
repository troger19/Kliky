package com.itible.exercise.util;

import java.text.SimpleDateFormat;

public class Util {

    public static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

    public static String removeLastComma(String repsToSave) {
        if (repsToSave.endsWith(",")) {
            return repsToSave.substring(0, repsToSave.length() - 1);
        } else {
            return repsToSave;
        }
    }
}
