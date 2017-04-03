package edu.rowanuniversity.rufit.rufitObjects;

/**
 * Created by catherine on 4/2/2017.
 */

public class Shoe {
    private String name;
    private double mileage;

    public Shoe(String name) {
        this.name = name;
        mileage = 0.0;
    }

    public Shoe() {    }

    public double getMileage() { return mileage; }

    public void setMileage(double mileage) { this.mileage = mileage;    }

    public String getName() {  return name;    }

    public void setName(String name) {  this.name = name;    }
}
