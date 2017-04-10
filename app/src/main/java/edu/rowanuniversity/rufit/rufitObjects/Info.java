package edu.rowanuniversity.rufit.rufitObjects;

/**
 * Created by catherine on 4/9/2017.
 */

public class Info {
    private String dob;
    private String gender;
    private int height;
    private int weight;
    private String username;

    public Info() {}

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getGender() {return gender;}

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getDob() {return dob;}

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}