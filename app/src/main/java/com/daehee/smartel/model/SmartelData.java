package com.daehee.smartel.model;

/**
 * Created by daehee on 2017. 9. 12..
 */

public class SmartelData {

    private String txtPhone = "";
    private String txtName = "";
    private String txtPw = "";

    private String leftCall = "";
    private String leftSms = "";
    private String leftData = "";

    public SmartelData(String txtPhone, String txtName, String txtPw) {
        this.txtPhone = txtPhone;
        this.txtName = txtName;
        this.txtPw = txtPw;
    }

    public String getTxtPhone() {
        return txtPhone;
    }

    public void setTxtPhone(String txtPhone) {
        this.txtPhone = txtPhone;
    }

    public String getTxtName() {
        return txtName;
    }

    public void setTxtName(String txtName) {
        this.txtName = txtName;
    }

    public String getTxtPw() {
        return txtPw;
    }

    public void setTxtPw(String txtPw) {
        this.txtPw = txtPw;
    }

    public String getLeftCall() {
        return leftCall;
    }

    public void setLeftCall(String leftCall) {
        this.leftCall = leftCall;
    }

    public String getLeftSms() {
        return leftSms;
    }

    public void setLeftSms(String leftSms) {
        this.leftSms = leftSms;
    }

    public String getLeftData() {
        return leftData;
    }

    public void setLeftData(String leftData) {
        this.leftData = leftData;
    }
} // end of class
