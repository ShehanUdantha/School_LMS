package com.firstprog.universityitschool.Model;

public class BatchModel {
    public String batchID, mainChildVal;

    public BatchModel(){}

    public BatchModel(String batchID, String mainChildVal) {
        this.batchID = batchID;
        this.mainChildVal = mainChildVal;
    }

    public String getBatchID() {
        return batchID;
    }

    public void setBatchID(String batchID) {
        this.batchID = batchID;
    }

    public String getMainChildVal() {
        return mainChildVal;
    }

    public void setMainChildVal(String mainChildVal) {
        this.mainChildVal = mainChildVal;
    }
}
