package edu.rowanuniversity.rufit.rufitObjects;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by catherine on 3/25/2017.
 * Last Updated: 04.08.2017
 *
 * Represents user within database.
 * TODO: May be able to remove records, runs, and shoes, variables from this class.
 *      They may prove unnecessary for our implementation of app.
 */

public class User {
    private String dob;
    private String gender;
    private int height;
    private int weight;
    private String username;
    private Goal goals;
    private Record records;
    private ArrayList<Run> runs;
    private HashMap<String,Shoe> shoes;


    public User() {
        username = "";
        dob = "";
        gender = "";
        height = 0;
        weight = 0;
        goals = new Goal();
        records = new Record();
        runs = new ArrayList<Run>();
        shoes = new HashMap<String,Shoe>();

    }

    public User(String dob, String gender, int height, int weight, String username,
                Goal goals, Record records, ArrayList<Run> runs, HashMap<String,Shoe> shoes) {
        this.dob = dob;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.username = username;
        this.goals = goals;
        this.records = records;
        this.runs = runs;
        this.shoes = shoes;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getGender() { return gender; }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getdob() { return dob;}

    public void setdob(String dob) {
        this.dob = dob;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Goal getGoals() {
        return goals;
    }
}
