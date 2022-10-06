package com.firstprog.universityitschool.Model;

public class SemesterModel {
    String semID, semMainChildID;
    boolean current;

    public SemesterModel(){}

    public SemesterModel(String semID, String semMainChildID, boolean current) {
        this.semID = semID;
        this.semMainChildID = semMainChildID;
        this.current = current;
    }

    public String getSemID() {
        return semID;
    }

    public void setSemID(String semID) {
        this.semID = semID;
    }

    public String getSemMainChildID() {
        return semMainChildID;
    }

    public void setSemMainChildID(String semMainChildID) {
        this.semMainChildID = semMainChildID;
    }

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }
}
