package com.firstprog.universityitschool.Model;

public class AssignmentsModel {
    public String assignmentsMainChildID, assignmentName, assignmentUploadDate, assignmentUrl;

    public AssignmentsModel(){}

    public AssignmentsModel(String assignmentsMainChildID, String assignmentName, String assignmentUploadDate, String assignmentUrl) {
        this.assignmentsMainChildID = assignmentsMainChildID;
        this.assignmentName = assignmentName;
        this.assignmentUploadDate = assignmentUploadDate;
        this.assignmentUrl = assignmentUrl;
    }

    public String getAssignmentsMainChildID() {
        return assignmentsMainChildID;
    }

    public void setAssignmentsMainChildID(String assignmentsMainChildID) {
        this.assignmentsMainChildID = assignmentsMainChildID;
    }

    public String getAssignmentName() {
        return assignmentName;
    }

    public void setAssignmentName(String assignmentName) {
        this.assignmentName = assignmentName;
    }

    public String getAssignmentUploadDate() {
        return assignmentUploadDate;
    }

    public void setAssignmentUploadDate(String assignmentUploadDate) {
        this.assignmentUploadDate = assignmentUploadDate;
    }

    public String getAssignmentUrl() {
        return assignmentUrl;
    }

    public void setAssignmentUrl(String assignmentUrl) {
        this.assignmentUrl = assignmentUrl;
    }
}
