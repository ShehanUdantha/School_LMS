package com.firstprog.universityitschool.Model;

public class QuizAddModel {

    String question, quizNewMainChildID, correct_letter, answer_four, answer_three, answer_two, answer_one;

    public QuizAddModel() {
    }

    public QuizAddModel(String question, String quizNewMainChildID, String correct_letter, String answer_four, String answer_three, String answer_two, String answer_one) {
        this.question = question;
        this.quizNewMainChildID = quizNewMainChildID;
        this.correct_letter = correct_letter;
        this.answer_four = answer_four;
        this.answer_three = answer_three;
        this.answer_two = answer_two;
        this.answer_one = answer_one;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQuizNewMainChildID() {
        return quizNewMainChildID;
    }

    public void setQuizNewMainChildID(String quizNewMainChildID) {
        this.quizNewMainChildID = quizNewMainChildID;
    }

    public String getCorrect_letter() {
        return correct_letter;
    }

    public void setCorrect_letter(String correct_letter) {
        this.correct_letter = correct_letter;
    }

    public String getAnswer_four() {
        return answer_four;
    }

    public void setAnswer_four(String answer_four) {
        this.answer_four = answer_four;
    }

    public String getAnswer_three() {
        return answer_three;
    }

    public void setAnswer_three(String answer_three) {
        this.answer_three = answer_three;
    }

    public String getAnswer_two() {
        return answer_two;
    }

    public void setAnswer_two(String answer_two) {
        this.answer_two = answer_two;
    }

    public String getAnswer_one() {
        return answer_one;
    }

    public void setAnswer_one(String answer_one) {
        this.answer_one = answer_one;
    }
}
