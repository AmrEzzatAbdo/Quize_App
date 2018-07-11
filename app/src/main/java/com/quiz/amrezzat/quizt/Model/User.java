package com.quiz.amrezzat.quizt.Model;

/**
 * Created by amrezzat on 7/5/2018.
 */

public class User {
    private String userName;
    private String password;
    private String email;
    private String rate;
    private String lquistion;

    public User() {

    }

    public User(String userName, String password, String email,String rate,String lquistion) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.rate=rate;
        this.lquistion=lquistion;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }
}
