package com.quiz.amrezzat.quizt.Model;

import com.google.firebase.database.Exclude;

import java.util.Date;

/**
 * Created by amrezzat on 7/5/2018.
 */

public class Quition {
    private String quition;
    private String choice1;
    private String choice2;
    private String choice3;
    private String choice4;
    private String trueChoice;
    private Date date;
    private boolean booleanQuestion;
   // private String cuser;


    public Quition() {

    }

    public Quition(String quition, String choice1, String choice2, String choice3, String choice4, String trueChoice, Date date, boolean booleanQuestion/*,String cuser*/) {
        this.quition = quition;
        this.choice1 = choice1;
        this.choice2 = choice2;
        this.choice3 = choice3;
        this.choice4 = choice4;
        this.trueChoice = trueChoice;
        this.date = date;
        this.booleanQuestion = booleanQuestion;
        //this.cuser=cuser;
    }

    public String getQuition() {
        return quition;
    }

    public void setQuition(String quition) {
        this.quition = quition;
    }

    public String getChoice1() {
        return choice1;
    }

    public void setChoice1(String choice1) {
        this.choice1 = choice1;
    }

    public String getChoice2() {
        return choice2;
    }

    public void setChoice2(String choice2) {
        this.choice2 = choice2;
    }

    //to chick user answer
    public String getTrueChoice() {
        return trueChoice;
    }

    public void setTrueChoice(String trueChoice) {
        this.trueChoice = trueChoice;
    }
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isBooleanQuestion() {
        return booleanQuestion;
    }

    public void setBooleanQuestion(boolean booleanQuestion) {
        this.booleanQuestion = booleanQuestion;
    }

    public String getChoice3() {
        return choice3;
    }

    public void setChoice3(String choice3) {
        this.choice3 = choice3;
    }

    public String getChoice4() {
        return choice4;
    }

    public void setChoice4(String choice4) {
        this.choice4 = choice4;
    }
/*
    public String getcuser() {
        return cuser;
    }

    public void setcuser(String cuser) {
        this.cuser = cuser;
    }
*/
}
