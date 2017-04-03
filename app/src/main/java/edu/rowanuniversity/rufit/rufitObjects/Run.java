package edu.rowanuniversity.rufit.rufitObjects;

import java.util.Date;

/**
 * Created by catherine on 4/2/2017.
 */

public class Run {
    private String name;
    private Date date;
    private double mileage;
    private int time;
    private int pace;
    private String shoe;
    private String feel;
    private String type;
    private String notes;

    public Run(String name, Date date, double mileage, int time, String shoe,
               String feel, String type, String notes) {
        this.name = name;
        this.date = date;
        this.mileage = mileage;
        this.time = time;
        pace = calculatePace();
        this.shoe = shoe;
        this.feel = feel;
        this.type = type;
        this.notes = notes;
    }

    public Run() {

    }

    private int calculatePace() {
        double t = (double) time;
        double p = ((t/mileage) + (t%mileage))/ 60;
        double rounded =  Math.round(p * 100)/ 100;
        double toSeconds = rounded * 60;
        return (int) toSeconds;
    }

    public String getName() {
        return name;
    }

    public Date getDate() {
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

    public String getFeel() {
        return feel;
    }

    public String getType() {
        return type;
    }

    public String getNotes() {
        return notes;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setMileage(double mileage) {
        this.mileage = mileage;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void setShoe(String shoe) {
        this.shoe = shoe;
    }

    public void setFeel(String feel) {
        this.feel = feel;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
