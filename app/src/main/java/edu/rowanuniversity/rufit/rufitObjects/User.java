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

public class User<String,Object> extends HashMap<String,Object>{

    private Info info;
    private Goal goals;
    private Record records;
    private HashMap<String,Run> runs;
    private HashMap<String,Shoe> shoes;


    public User() {

    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public Record getRecords() {
        return records;
    }

    public void setRecords(Record records) {
        this.records = records;
    }

    public HashMap<String, Run> getRuns() {
        return runs;
    }

    public void setRuns(HashMap<String, Run> runs) {
        this.runs = runs;
    }

    public HashMap<String, Shoe> getShoes() {
        return shoes;
    }

    public void setShoes(HashMap<String, Shoe> shoes) {
        this.shoes = shoes;
    }

    public Goal getGoals() {
        return goals;
    }

    public void setGoals(Goal goals) { this.goals = goals;    }

    }
