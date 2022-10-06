package com.firstprog.universityitschool.Model;

public class UserModel {
    public String uid, firstName, lastName, email, indexNo, role, batch, img, pass, token;
    private boolean isExpanded;

    public UserModel() {
    }

    public UserModel(String uid, String firstName, String lastName, String email, String indexNo, String role, String batch, String img, String pass, String token) {
        this.firstName = firstName;
        this.uid = uid;
        this.lastName = lastName;
        this.email = email;
        this.indexNo = indexNo;
        this.role = role;
        this.batch = batch;
        this.img = img;
        this.pass = pass;
        this.isExpanded = false;
        this.token = token;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIndexNo() {
        return indexNo;
    }

    public void setIndexNo(String indexNo) {
        this.indexNo = indexNo;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
}
