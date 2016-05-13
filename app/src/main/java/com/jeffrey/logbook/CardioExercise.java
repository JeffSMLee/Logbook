package com.jeffrey.logbook;

/**
 * Created by Jeffrey on 5/13/2016.
 */
public class CardioExercise {

    private String name;
    private int hours;
    private int minutes;
    private int seconds;

    public CardioExercise(String name, int hours, int minutes, int seconds) {
        this.name = name;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
    }

    public String getName() {
        return name;
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getSeconds() {
        return seconds;
    }

}
