package com.capstone.app24.webservices_model;

import com.facebook.AccessToken;

/**
 * Created by amritpal on 25/11/15.
 */
public class UserLoginModel {
    String user_social_id;
    String user_email;
    String user_fname;
    String user_lname;
    String user_gender;
    String user_deviceType;
    String user_deviceToken;
    String user_loginType;
    String result;
    String response;

    public UserLoginModel() {

    }


    public UserLoginModel(String user_social_id, String user_email, String user_fname,
                          String user_lname, String user_gender, String user_deviceType,
                          String user_deviceToken, String user_loginType, String result, String response) {
        this.user_social_id = user_social_id;
        this.user_email = user_email;
        this.user_fname = user_fname;
        this.user_lname = user_lname;
        this.user_gender = user_gender;
        this.user_deviceType = user_deviceType;
        this.user_deviceToken = user_deviceToken;
        this.user_loginType = user_loginType;
        this.result = result;
        this.response = response;
    }


    public String getUser_social_id() {
        return user_social_id;
    }

    public void setUser_social_id(String user_social_id) {
        this.user_social_id = user_social_id;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_fname() {
        return user_fname;
    }

    public void setUser_fname(String user_fname) {
        this.user_fname = user_fname;
    }

    public String getUser_lname() {
        return user_lname;
    }

    public void setUser_lname(String user_lname) {
        this.user_lname = user_lname;
    }

    public String getUser_gender() {
        return user_gender;
    }

    public void setUser_gender(String user_gender) {
        this.user_gender = user_gender;
    }

    public String getUser_deviceType() {
        return user_deviceType;
    }

    public void setUser_deviceType(String user_deviceType) {
        this.user_deviceType = user_deviceType;
    }

    public String getUser_deviceToken() {
        return user_deviceToken;
    }

    public void setUser_deviceToken(String user_deviceToken) {
        this.user_deviceToken = user_deviceToken;
    }

    public String getUser_loginType() {
        return user_loginType;
    }

    public void setUser_loginType(String user_loginType) {
        this.user_loginType = user_loginType;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
