package edu.rowanuniversity.rufit.rufitObjects;

/**
 * Created by catherine on 3/25/2017.
 */

public class UserInfo {
    private int age;
    private String gender;
    private int height;
    private int weight;
    private  String username;

    public UserInfo() {

    }

    public UserInfo( int age, String gender, int height, int weight, String username) {
        this.age = age;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.username = username;
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

    public int getAge() { return age;
    }

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
