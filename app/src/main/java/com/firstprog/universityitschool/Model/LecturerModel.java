package com.firstprog.universityitschool.Model;

public class LecturerModel {
    String selected_lecturersID, selected_lecturersMainChildID, selected_lecturersName, selected_lecturersEmail, selected_lecturersIndexNo, selected_lecturersPic, selected_subjectsID;

    public LecturerModel(){}

    public LecturerModel(String selected_lecturersID, String selected_lecturersMainChildID, String selected_lecturersName, String selected_lecturersEmail, String selected_lecturersIndexNo, String selected_lecturersPic, String selected_subjectsID) {
        this.selected_lecturersID = selected_lecturersID;
        this.selected_lecturersMainChildID = selected_lecturersMainChildID;
        this.selected_lecturersName = selected_lecturersName;
        this.selected_lecturersEmail = selected_lecturersEmail;
        this.selected_lecturersIndexNo = selected_lecturersIndexNo;
        this.selected_lecturersPic = selected_lecturersPic;
        this.selected_subjectsID = selected_subjectsID;
    }

    public String getSelected_lecturersID() {
        return selected_lecturersID;
    }

    public void setSelected_lecturersID(String selected_lecturersID) {
        this.selected_lecturersID = selected_lecturersID;
    }

    public String getSelected_lecturersMainChildID() {
        return selected_lecturersMainChildID;
    }

    public void setSelected_lecturersMainChildID(String selected_lecturersMainChildID) {
        this.selected_lecturersMainChildID = selected_lecturersMainChildID;
    }

    public String getSelected_lecturersName() {
        return selected_lecturersName;
    }

    public void setSelected_lecturersName(String selected_lecturersName) {
        this.selected_lecturersName = selected_lecturersName;
    }

    public String getSelected_lecturersEmail() {
        return selected_lecturersEmail;
    }

    public void setSelected_lecturersEmail(String selected_lecturersEmail) {
        this.selected_lecturersEmail = selected_lecturersEmail;
    }

    public String getSelected_lecturersIndexNo() {
        return selected_lecturersIndexNo;
    }

    public void setSelected_lecturersIndexNo(String selected_lecturersIndexNo) {
        this.selected_lecturersIndexNo = selected_lecturersIndexNo;
    }

    public String getSelected_lecturersPic() {
        return selected_lecturersPic;
    }

    public void setSelected_lecturersPic(String selected_lecturersPic) {
        this.selected_lecturersPic = selected_lecturersPic;
    }

    public String getSelected_subjectsID() {
        return selected_subjectsID;
    }

    public void setSelected_subjectsID(String selected_subjectsID) {
        this.selected_subjectsID = selected_subjectsID;
    }
}
