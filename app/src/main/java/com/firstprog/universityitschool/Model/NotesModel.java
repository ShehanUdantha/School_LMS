package com.firstprog.universityitschool.Model;

public class NotesModel {
    String notesMainChildID, notesPriority, notesSubtitle, note, notesTitle, noteDate;


    public NotesModel(){}

    public NotesModel(String notesMainChildID, String notesPriority, String notesSubtitle, String note, String notesTitle, String noteDate) {
        this.notesMainChildID = notesMainChildID;
        this.notesPriority = notesPriority;
        this.notesSubtitle = notesSubtitle;
        this.note = note;
        this.notesTitle = notesTitle;
        this.noteDate = noteDate;
    }

    public String getNotesMainChildID() {
        return notesMainChildID;
    }

    public void setNotesMainChildID(String notesMainChildID) {
        this.notesMainChildID = notesMainChildID;
    }

    public String getNotesPriority() {
        return notesPriority;
    }

    public void setNotesPriority(String notesPriority) {
        this.notesPriority = notesPriority;
    }

    public String getNotesSubtitle() {
        return notesSubtitle;
    }

    public void setNotesSubtitle(String notesSubtitle) {
        this.notesSubtitle = notesSubtitle;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getNotesTitle() {
        return notesTitle;
    }

    public void setNotesTitle(String notesTitle) {
        this.notesTitle = notesTitle;
    }

    public String getNoteDate() {
        return noteDate;
    }

    public void setNoteDate(String noteDate) {
        this.noteDate = noteDate;
    }
}
