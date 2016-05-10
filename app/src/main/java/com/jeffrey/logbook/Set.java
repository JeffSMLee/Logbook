package com.jeffrey.logbook;

/**
 * Created by Jeffrey on 4/20/2016.
 */
public class Set {

    private double weight;
    private int reps;

    public Set(double weight, int reps) {
        this.weight = weight;
        this.reps = reps;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

}
