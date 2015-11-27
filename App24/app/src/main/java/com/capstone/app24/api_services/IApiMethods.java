package com.capstone.app24.api_services;

/**
 * Created by amritpal on 24/11/15.
 */

import com.capstone.app24.utils.APIsConstants;
import com.capstone.app24.utils.Constants;
import com.capstone.app24.webservices_model.UserLoginModel;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;


public interface IApiMethods {

//    @GET("/user_info&customer_id={id}")
//    UserLoginModel getUser(
//            @Path("id") String key
//    );

    //    public void getUserLoginModel(@Body UserLoginModel userLoginModel, Callback<UserLoginModel>
//            result);
    @FormUrlEncoded
    @POST("/facebooklogin")
    void getUserLoginModel(@Field("user_social_id") String user_social_id, @Field("user_email")
    String user_email, @Field("user_fname") String user_fname, @Field("user_lname")
                           String user_lname, @Field("user_gender") String user_gender, @Field("user_deviceType")
                           String user_deviceType, @Field("user_deviceToken") String user_deviceToken,
                           @Field("user_loginType") String user_loginType, Callback<UserLoginModel>
                                   result);

}
