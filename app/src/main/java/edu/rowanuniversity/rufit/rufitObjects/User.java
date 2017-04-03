package edu.rowanuniversity.rufit.rufitObjects;

import java.util.ArrayList;

/**
 * Created by catherine on 3/25/2017.
 */

public class User {
    private int age;
    private String gender;
    private int height;
    private int weight;
    private String username;
    private Goal goals;
    private Record records;
    private ArrayList<Run> runs;
    private ArrayList<Shoe> shoes;


    public User() {
        username = "";
        age = 0;
        gender = "";
        height = 0;
        weight = 0;
        goals = new Goal();
        records = new Record();
        runs = new ArrayList<Run>();
        shoes = new ArrayList<Shoe>();

    }

    public User(int age, String gender, int height, int weight, String username,
                Goal goals, Record records, ArrayList<Run> runs, ArrayList<Shoe> shoes) {
        this.age = age;
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

    public int getAge() { return age;}

    public void setAge(int age) {
        this.age = age;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
