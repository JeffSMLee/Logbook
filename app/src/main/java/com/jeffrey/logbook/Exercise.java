package com.jeffrey.logbook;

import java.util.List;

/**
 * Created by Jeffrey on 5/30/2016.
 */
public class Exercise {

    private String name;
    private List<Input> inputs;

    public Exercise(String name, List<Input> inputs) {
        this.name = name;
        this.inputs = inputs;
    }

    public String getName() {
        return name;
    }

    public List<Input> getInputs() {
        return inputs;
    }

    public enum Input {
        WEIGHT, REPS, TIME, DISTANCE
    }
}
