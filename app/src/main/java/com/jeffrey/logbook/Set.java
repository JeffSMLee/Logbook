package com.jeffrey.logbook;

/**
 * Created by Jeffrey on 4/20/2016.
 */
public class Set {

    private double weight;
    private int reps;
    private String time;
    private double distance;

    public Set setWeight(double weight) {
        this.weight = weight;
        return this;
    }

    public Set setReps(int reps) {
        this.reps = reps;
        return this;
    }

    public Set setTime(String time) {
        this.time = time;
        return this;
    }

    public Set setDistance(double distance) {
        this.distance = distance;
        return this;
    }

    public double getWeight() {
        return weight;
    }

    public int getReps() {
        return reps;
    }

    public String getTime() {
        return time;
    }

    public double getDistance() {
        return distance;
    }
}
