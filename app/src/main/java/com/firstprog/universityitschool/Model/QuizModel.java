package com.firstprog.universityitschool.Model;

public class QuizModel {
    String quizID, quizMainChildID;

    public QuizModel(){}

    public QuizModel(String quizID, String quizMainChildID) {
        this.quizID = quizID;
        this.quizMainChildID = quizMainChildID;
    }

    public String getQuizID() {
        return quizID;
    }

    public void setQuizID(String quizID) {
        this.quizID = quizID;
    }

    public String getQuizMainChildID() {
        return quizMainChildID;
    }

    public void setQuizMainChildID(String quizMainChildID) {
        this.quizMainChildID = quizMainChildID;
    }
}
