package edu.rowanuniversity.rufit.rufitObjects;

/**
 * Created by catherine on 3/29/2017.
 */

public class Goal {

    private int daysPerWeek;
    private int milesPerWeek;
    private int daysUntilRace;


    public Goal(int dpwValue, int mpwValue, int durValue) {
        daysPerWeek = dpwValue;
        milesPerWeek = mpwValue;
        daysUntilRace = durValue;
    }

    public int getDaysUntilRace() {
        return daysUntilRace;
    }

    public void setDaysUntilRace(int daysUntilRace) {
        this.daysUntilRace = daysUntilRace;
    }

    public int getMilesPerWeek() {
        return milesPerWeek;
    }

    public void setMilesPerWeek(int milesPerWeek) {
        this.milesPerWeek = milesPerWeek;
    }

    public int getDaysPerWeek() {
        return daysPerWeek;
    }

    public void setDaysPerWeek(int daysPerWeek) {
        this.daysPerWeek = daysPerWeek;
    }
}
