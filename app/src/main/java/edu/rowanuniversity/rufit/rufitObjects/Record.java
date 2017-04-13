package edu.rowanuniversity.rufit.rufitObjects;

import java.util.ArrayList;

/**
 * Created by catherine on 4/2/2017.
 */

public class Record {

    private double recordDistance;
    private int recordPace;
    private int recordTime;

    public Record () {
        recordDistance = -1.0;
        recordPace = -1;
        recordTime = -1;
    }

    /**
     * constructor with runs arraylist as params to sort through for record data
     * @param runs
     */
    public Record (ArrayList<Run> runs) {
        double tempRDistance = -1.0;
        int tempRPace = -1;
        int tempRTime = -1;

        for(Run run : runs) {
            if(tempRDistance < run.getMileage())
                tempRDistance = run.getMileage();
            if(tempRPace < run.getPace())
                tempRPace = run.getPace();
            if(tempRTime < run.getTime())
                tempRTime = run.getTime();
        }

        this.recordDistance = tempRDistance;
        this.recordPace = tempRPace;
        this.recordTime = tempRTime;
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
