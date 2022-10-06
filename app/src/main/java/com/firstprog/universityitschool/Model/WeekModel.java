package com.firstprog.universityitschool.Model;

public class WeekModel {

    String weekID, weekMainChildID;
    boolean weekCurrent;

    public WeekModel(){}

    public WeekModel(String weekID, String weekMainChildID, boolean weekCurrent) {
        this.weekID = weekID;
        this.weekMainChildID = weekMainChildID;
        this.weekCurrent = weekCurrent;
    }

    public String getWeekID() {
        return weekID;
    }

    public void setWeekID(String weekID) {
        this.weekID = weekID;
    }

    public String getWeekMainChildID() {
        return weekMainChildID;
    }

    public void setWeekMainChildID(String weekMainChildID) {
        this.weekMainChildID = weekMainChildID;
    }

    public boolean isWeekCurrent() {
        return weekCurrent;
    }

    public void setWeekCurrent(boolean weekCurrent) {
        this.weekCurrent = weekCurrent;
    }
}

