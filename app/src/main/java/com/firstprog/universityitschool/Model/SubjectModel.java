package com.firstprog.universityitschool.Model;

public class SubjectModel {
    public String subID, subMainChildID;

    public SubjectModel(){}

    public SubjectModel(String subID, String subMainChildID) {
        this.subID = subID;
        this.subMainChildID = subMainChildID;
    }

    public String getSubID() {
        return subID;
    }

    public void setSubID(String subID) {
        this.subID = subID;
    }

    public String getSubMainChildID() {
        return subMainChildID;
    }

    public void setSubMainChildID(String subMainChildID) {
        this.subMainChildID = subMainChildID;
    }
}
