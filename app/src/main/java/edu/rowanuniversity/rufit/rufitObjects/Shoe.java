package edu.rowanuniversity.rufit.rufitObjects;

/**
 * Created by catherine on 4/2/2017.
 * Last Updated: 04.08.2017
 *
 * Represents a shoe object and data necessary for log associated with it.
 */

public class Shoe {
    private String name;
    private double mileage;
    private int runs;
    private int time;

    public Shoe(String name) {
        this.name = name;
        mileage = 0.0;
        runs = 0;
        time = 0;
    }

    public Shoe() {    }

    public double getMileage() { return mileage; }

    public void setMileage(double mileage) { this.mileage = mileage;    }

    public void addMileage(double miles) { mileage += miles; }

    public String getName() {  return name;    }

    public void setName(String name) {  this.name = name;    }

    public int getRuns() { return runs;    }

    public void setRuns(int runs) { this.runs = runs; }

    public int getTimeRan() { return time; }

    public void setTimeRan(int time) { this.time = time; }
}
