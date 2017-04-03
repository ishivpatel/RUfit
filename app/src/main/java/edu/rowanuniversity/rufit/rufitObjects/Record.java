package edu.rowanuniversity.rufit.rufitObjects;

/**
 * Created by catherine on 4/2/2017.
 */

public class Record {

    private int recordPace;
    private double recordDistance;
    private int recordTime;

    public Record () {
        recordDistance = -1.0;
        recordPace = -1;
        recordTime = -1;
    }

    public int getRecordPace() {
        return recordPace;
    }

    public double getRecordDistance() {
        return recordDistance;
    }

    public int getRecordTime() {
        return recordTime;
    }

    public void setRecordPace(int recordPace) {
        this.recordPace = recordPace;
    }

    public void setRecordDistance(double recordDistance) {
        this.recordDistance = recordDistance;
    }

    public void setRecordTime(int recordTime) {
        this.recordTime = recordTime;
    }
}
