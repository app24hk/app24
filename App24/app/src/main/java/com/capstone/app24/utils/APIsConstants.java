package com.capstone.app24.utils;

/**
 * Created by amritpal on 25/11/15.
 */
public class APIsConstants {
    public static final String API_BASE_URL = "http://dev614.trigma.us/24app/development/Users";

    //Web Services
    public static final String API_FACEBOOK_LOGIN = "/facebooklogin";
    public static final String API_SAVE_FEEDS = "/saveFeeds";
    public static final String API_RECENT_FEEDS = "/recentFeed";
    public static final String API_LOGOUT = "/logout";


    public static final String KEY_RESULT = "result";
    public static final String KEY_USERINFO = "userInfo";

    //    facebook login response
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USER_EMAIL = "user_email";
    public static final String KEY_USER_FNAME = "user_fname";
    public static final String KEY_USER_LNAME = "user_lname";
    public static final String KEY_USER_GENDER = "user_gender";
    public static final String KEY_USER_NAME = "user_name";
    public static final String KEY_USER_LOGINTYPE = "user_loginType";
    public static final String KEY_USER_SOCIAL_ID = "user_social_id";

    public static final String KEY_MESSAGE = "message";

    //Post Feed
    public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_MEDIA = "media";
    public static final String KEY_TYPE = "type";

    //Recent Feeds
    public static final String KEY_PAGE_NUMBER = "page_number";
    public static final String KEY_FEED_DATA = "feedData";
    public static final String KEY_ID = "id";
    public static final String KEY_CREATED = "created";
    public static final String KEY_MODIFIED = "modified";
    public static final String KEY_VIEWCOUNT = "viewcount";
    public static final String KEY_THUMBNAIL = "thumbnail";
}
