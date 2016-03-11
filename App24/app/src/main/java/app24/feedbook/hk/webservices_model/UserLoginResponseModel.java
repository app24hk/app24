package app24.feedbook.hk.webservices_model;

import app24.feedbook.hk.utils.Utils;

/**
 * Created by amritpal on 25/11/15.
 */
public class UserLoginResponseModel {

    private static final String TAG = UserLoginResponseModel.class.getSimpleName();
    boolean result;
    String user_id;
    String user_email;
    String user_fname;
    String user_lname;
    String user_gender;
    String user_name;
    String user_loginType;
    String user_social_id;
    String message;

    public UserLoginResponseModel() {
    }

    public UserLoginResponseModel(boolean result, String user_id, String user_email, String
            user_fname, String
                                          user_lname, String user_gender, String user_name, String user_loginType, String
                                          user_social_id, String message) {
        this.result = result;
        this.user_id = user_id;
        this.user_email = user_email;
        this.user_fname = user_fname;
        this.user_lname = user_lname;
        this.user_gender = user_gender;
        this.user_name = user_name;
        this.user_loginType = user_loginType;
        this.user_social_id = user_social_id;
        this.message = message;
    }


    public UserLoginResponseModel(String user_id, String user_email, String user_fname, String
            user_lname, String user_gender, String user_name, String user_loginType, String
                                          user_social_id) {
        this.user_id = user_id;
        this.user_email = user_email;
        this.user_fname = user_fname;
        this.user_lname = user_lname;
        this.user_gender = user_gender;
        this.user_name = user_name;
        this.user_loginType = user_loginType;
        this.user_social_id = user_social_id;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_loginType() {
        return user_loginType;
    }

    public void setUser_loginType(String user_loginType) {
        this.user_loginType = user_loginType;
    }

    public String getUser_social_id() {
        return user_social_id;
    }

    public void setUser_social_id(String user_social_id) {
        this.user_social_id = user_social_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public void show() {
        try {
            Utils.debug(TAG, getUser_id());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Utils.debug(TAG, getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Utils.debug(TAG, getUser_email());
            Utils.debug(TAG, getUser_loginType());
            Utils.debug(TAG, getUser_social_id());
            Utils.debug(TAG, getUser_gender());
            Utils.debug(TAG, getUser_fname());
            Utils.debug(TAG, getUser_lname());
            Utils.debug(TAG, getUser_name());
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
