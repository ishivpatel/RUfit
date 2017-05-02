package edu.rowanuniversity.rufit.rufitObjects;

import java.io.Serializable;

import edu.rowanuniversity.rufit.StartRunActivity;

/**
 * Created by catherine on 4/2/2017.
 */

public class Run implements Serializable {
    private String id;
    private String name;
    private String date;
    private double mileage;
    private int time;
    private int pace;
    private String shoe;
    private int feel;
    private String type;
    private String notes;
    private int calories;

    public Run(String name, String date, double mileage, int time, String shoe,
               int feel, String type, String notes, int calories) {
        this.name = name;
        this.date = date;
        this.mileage = mileage;
        this.time = time;
        this.shoe = shoe;
        this.feel = feel;
        this.type = type;
        this.notes = notes;
        this.calories = calories;
    }

    public Run() { }

    public void calculatePace() {
        if(time > -1 && mileage > -1.0) {
            double p = ((time / mileage) + (time % mileage));
            setPace((int) p);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public double getMileage() {
        return mileage;
    }

    public int getTime() {
        return time;
    }

    public int getPace() {
        return pace;
    }

    public String getShoe() {
        return shoe;
    }

    public int getFeel() {
        return feel;
    }

    public String getType() {
        return type;
    }

    public String getNotes() {
        return notes;
    }

    public void setPace(int pace) { this.pace = pace; }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setMileage(double mileage) {
        this.mileage = mileage;
        calculatePace();
    }

    public void setTime(int time) {
        this.time = time;
        calculatePace();
    }

    public void setShoe(String shoe) {
        this.shoe = shoe;
    }

    public void setFeel(int feel) {
        this.feel = feel;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
