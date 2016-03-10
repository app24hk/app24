package com.capstone.hk.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.capstone.hk.R;
import com.capstone.hk.utils.APIsConstants;
import com.capstone.hk.utils.AlertToastManager;
import com.capstone.hk.utils.AppController;
import com.capstone.hk.utils.Constants;
import com.capstone.hk.utils.NetworkUtils;
import com.capstone.hk.utils.Utils;
import com.capstone.hk.webservices_model.UserLoginModel;
import com.capstone.hk.webservices_model.UserLoginResponseModel;
import com.crittercism.app.Crittercism;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import volley.Request;
import volley.VolleyError;
import volley.VolleyLog;
import volley.toolbox.StringRequest;

/**
 * Created by amritpal on 2/11/15.
 */
public class SplashActivity extends Activity implements View.OnClickListener {

    private static final String TAG = SplashActivity.class.getSimpleName();
    private TextView txt_terms_of_use;
    private Button btn_login_with_facebook;
    private LoginButton fb_btn;

    CallbackManager callbackManager;
    AccessToken accessToken;
    private UserLoginModel userModel;
    private UserLoginResponseModel mUserBeanResponse;
    private SweetAlertDialog mDialog;

    /* Volley Request Tags */
    private String res = "";
    private AccessTokenTracker mAccessTokenTracker;
    private String mAccessToken;
    private String mAndroidDeviceId = null;
    /* End of Volley Request Tags */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crittercism.initialize(getApplicationContext(), Constants.CRITTERCISM_APP_ID);
        //.............Facebook SDK Initialization...............
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        new Utils(this).setPreferences(this, Constants.FETCH_GALLERY_IMAGE, true);
        new Utils(this).setPreferences(this, Constants.FETCH_GALLERY_VIDEO, true);
        new Utils(this).setPreferences(this, Constants.FETCH_GALLERY_IMAGE_AND_VIDEOS, true);

        Locale locale = new Locale("zh");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        setContentView(R.layout.activity_splash);
        if (new Utils(this).getSharedPreferences(this, Constants.KEY_IS_LOGGED_IN, false)) {
            Utils.debug("accessToken", "AccessToken.getCurrentAccessToken() : " + AccessToken.getCurrentAccessToken());
            finish();
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
        }
        txt_terms_of_use = (TextView) findViewById(R.id.txt_terms_of_use);
        btn_login_with_facebook = (Button) findViewById(R.id.btn_login_with_facebook);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        initialize();
        setClickListeners();
        updateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void initialize() {

        txt_terms_of_use = (TextView) findViewById(R.id.txt_terms_of_use);
        btn_login_with_facebook = (Button) findViewById(R.id.btn_login_with_facebook);
        fb_btn = (LoginButton) findViewById(R.id.login_button);
        getKeyHash();
        facebookIntegration();
    }

    private void setClickListeners() {
        txt_terms_of_use.setOnClickListener(this);
        btn_login_with_facebook.setOnClickListener(this);
        mAccessTokenTracker = new AccessTokenTracker() {

            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

                Utils.info(TAG, "AccessTokenTracker oldAccessToken: " + oldAccessToken + "" +
                        " " +
                        "- " +
                        "currentAccessToken: " + currentAccessToken);
            }
        };
    }

    private void updateUI() {
        SpannableString content = new SpannableString(getResources().getString(R.string.terms_of_use));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        txt_terms_of_use.append(content);
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.txt_terms_of_use:
                intent = new Intent(SplashActivity.this, TermsNConditionsActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_login_with_facebook:
                mDialog = Utils.showSweetProgressDialog(SplashActivity.this,
                        getResources
                                ().getString(R.string.progress_loading), SweetAlertDialog.PROGRESS_TYPE);
                fb_btn.performClick();
                break;
            default:
                break;
        }
    }

    /**
     * Facebook Integration and Login Response. get AccessToken and user details. These details
     * sent to web services for User register
     */
    public void facebookIntegration() {
        //fb_btn.setPublishPermissions(Arrays.asList("publish_pages,manage_pages"));
        fb_btn.setReadPermissions(Arrays.asList("public_profile, email, user_birthday," +
                "user_friends,publish_pages,publish_actions"));

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        accessToken = loginResult.getAccessToken();
                        Utils.debug(TAG, "accessToken : " + accessToken);
                        Utils.debug(TAG, "accessToken : " + accessToken.getToken());
                        Utils.debug(TAG, "accessToken : " + accessToken.getPermissions());
                        new Utils().setPreferences(SplashActivity.this, Constants.KEY_FACEBOOK_ACCESS_TOKEN,
                                accessToken + ""); //
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONObject object,
                                            GraphResponse response) {
                                        // Application code
                                        Utils.debug(TAG, response.getJSONObject()
                                                .toString());
                                        try {
                                            JSONObject jsonObject = response.getJSONObject();

                                            String email = "", username, pictureobj, url, facebookid = "", last_name = "",
                                                    first_name = "", gender = "";
                                            if (jsonObject.has(Constants.KEY_LAST_NAME)) {
                                                last_name = jsonObject.getString(Constants.KEY_LAST_NAME);
                                            }
                                            if (jsonObject.has(Constants.KEY_ID)) {
                                                facebookid = jsonObject.getString(Constants.KEY_ID);
                                            }
                                            if (jsonObject.has(Constants.KEY_GENDER)) {
                                                gender = jsonObject.getString(Constants.KEY_GENDER);
                                            }
                                            if (jsonObject.has(Constants.KEY_FIRST_NAME)) {
                                                first_name = jsonObject.getString(Constants.KEY_FIRST_NAME);
                                            }
                                            if (jsonObject.has(Constants.KEY_EMAIL)) {
                                                email = jsonObject.getString(Constants.KEY_EMAIL);
                                            }
                                            if (jsonObject.has(Constants.KEY_NAME)) {
                                                username = jsonObject.getString(Constants.KEY_NAME);
                                            }

                                            URL image_value = new URL("https://graph.facebook.com/" + facebookid + "/picture?type=large");
                                            pictureobj = image_value.toString();
                                            Utils.debug(TAG, "pictureobj : " + pictureobj);
                                            mAndroidDeviceId = Settings.Secure.getString
                                                    (getContentResolver(),
                                                            Settings.Secure.ANDROID_ID);
                                            Utils.debug(TAG, "" + mAndroidDeviceId);
                                            //Saving Profile picture
                                            new Utils(SplashActivity.this).setPreferences
                                                    (SplashActivity.this, Constants.FB_IMAGE_SMALL, pictureobj);

                                            userModel = new UserLoginModel(facebookid, email,
                                                    first_name, last_name, gender, Constants.ANDROID,
                                                    mAndroidDeviceId, Constants.FACEBOOK);

                                            //Register user to Server
                                            if (NetworkUtils.isOnline(SplashActivity.this)) {
                                                makeUserLoginRequest();
                                            } else {
                                                Utils.showSweetProgressDialog(SplashActivity.this,
                                                        getResources().getString(R.string
                                                                .check_your_internet_connection),
                                                        SweetAlertDialog.ERROR_TYPE);
                                            }
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        }

                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,first_name,last_name,email,gender, birthday,picture");
                        request.setParameters(parameters);
                        request.executeAsync();

                    }

                    @Override
                    public void onCancel() {
                        // App code
                        AlertToastManager.showToast(getResources().getString(R.string.please_try_again),
                                SplashActivity
                                        .this);
                        Utils.closeSweetProgressDialog(SplashActivity.this, mDialog);
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        AlertToastManager.showToast(getResources().getString(R.string
                                .error_occured), SplashActivity.this);
                        Utils.closeSweetProgressDialog(SplashActivity.this, mDialog);
                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    public void getKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.capstone.app24",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Utils.debug("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }
    //...............Volley String Request..............

    public boolean makeUserLoginRequest() {
        /*
         Starting a progress Dialog...
         If second parameter is passed null then progressdialog will show (Loading...) by default if pass string such as(Searching..) then
         it will show (Searching...)
         */
        StringRequest strReq = new StringRequest(Request.Method.POST,
                APIsConstants.API_BASE_URL + APIsConstants.API_FACEBOOK_LOGIN,
                new volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Utils.debug(TAG, response.toString());
                        if (mDialog != null)
                            Utils.closeSweetProgressDialog(SplashActivity.this, mDialog);
                        res = response.toString();
                        try {
                            setUserData(res);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.closeSweetProgressDialog(SplashActivity.this, mDialog);
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                res = error.toString();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(APIsConstants.KEY_USER_SOCIAL_ID, userModel.getUser_social_id());
                params.put(APIsConstants.KEY_USER_EMAIL, userModel.getUser_email());
                params.put(APIsConstants.KEY_USER_FNAME, userModel.getUser_fname());
                params.put(APIsConstants.KEY_USER_LNAME, userModel.getUser_lname());
                params.put(APIsConstants.KEY_USER_GENDER, userModel.getUser_gender());
                params.put(APIsConstants.KEY_USER_DEVICE_TYPE, userModel.getUser_deviceType());
                params.put(APIsConstants.KEY_USER_DEVICE_TOKEN, userModel.getUser_deviceToken());
                params.put(APIsConstants.KEY_USER_LOGINTYPE, Constants.FACEBOOK);
                Utils.info("params...", params.toString());
                return params;
            }
            // Adding request to request queue
        };
        AppController.getInstance().addToRequestQueue(strReq, Constants.ADD_TO_QUEUE);
        return false;
    }

    private void setUserData(String res) throws JSONException {
        JSONObject jsonObject = new JSONObject(res);
        if (jsonObject.getBoolean(APIsConstants.KEY_RESULT)) {
            JSONObject userInfoObject;
            userInfoObject = jsonObject.getJSONObject(APIsConstants.KEY_USERINFO);
            if (mUserBeanResponse == null)
                mUserBeanResponse = new UserLoginResponseModel();
            try {
                mUserBeanResponse.setResult(jsonObject.getBoolean(APIsConstants.KEY_RESULT));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                mUserBeanResponse.setUser_id(userInfoObject.getString(APIsConstants.KEY_USER_ID));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                mUserBeanResponse.setUser_email(userInfoObject.getString(APIsConstants.KEY_USER_EMAIL));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                mUserBeanResponse.setUser_fname(userInfoObject.getString(APIsConstants.KEY_USER_FNAME));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                mUserBeanResponse.setUser_lname(userInfoObject.getString(APIsConstants.KEY_USER_LNAME));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                mUserBeanResponse.setUser_gender(userInfoObject.getString(APIsConstants.KEY_USER_GENDER));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                mUserBeanResponse.setUser_name(userInfoObject.getString(APIsConstants.KEY_USER_NAME));
                new Utils().setPreferences(SplashActivity.this, Constants.KEY_USER_NAME,
                        userInfoObject.getString(APIsConstants.KEY_USER_NAME));

            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                mUserBeanResponse.setUser_loginType(userInfoObject.getString(APIsConstants.KEY_USER_LOGINTYPE));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                mUserBeanResponse.setUser_social_id(userInfoObject.getString(APIsConstants.KEY_USER_SOCIAL_ID));
            } catch (Exception e) {
                e.printStackTrace();
            }

            mUserBeanResponse.show();

            if (mUserBeanResponse.getUser_id() != null) {
                new Utils(SplashActivity
                        .this).setPreferences
                        (SplashActivity.this, Constants
                                .KEY_IS_LOGGED_IN, true);
                new Utils().setPreferences(SplashActivity.this, Constants.KEY_USER_DETAILS,
                        mUserBeanResponse.getUser_id());
                new Utils().setPreferences(SplashActivity.this, Constants.KEY_USER_NAME,
                        mUserBeanResponse.getUser_name());
                new Utils().setPreferences(SplashActivity.this, Constants.KEY_FACEBOOK_ACCESS_TOKEN,
                        mAndroidDeviceId);
                new Utils().setPreferences(SplashActivity.this, Constants.KEY_FACEBOOK_ID,
                        mUserBeanResponse.getUser_social_id());
                Utils.closeSweetProgressDialog(SplashActivity.this, mDialog);
                finish();
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
                Utils.closeSweetProgressDialog(SplashActivity.this, mDialog);
            }
            Utils.closeSweetProgressDialog(SplashActivity.this, mDialog);
        } else if (jsonObject.has(Constants.LOGGED_IN)) {
            LoginManager.getInstance().logOut();
            mDialog = Utils.showSweetProgressDialog(this, jsonObject.getString(APIsConstants
                            .KEY_MESSAGE),
                    SweetAlertDialog.ERROR_TYPE);

        } else {
            try {
                Utils.debug(Constants.API_TAG, jsonObject.getString(APIsConstants.KEY_MESSAGE));
                Utils.showSweetProgressDialog(SplashActivity.this, jsonObject.getString(APIsConstants
                        .KEY_MESSAGE), SweetAlertDialog.ERROR_TYPE);
                Utils.closeSweetProgressDialog(SplashActivity.this, mDialog);

            } catch (Exception e) {
                e.printStackTrace();
            }
            Utils.closeSweetProgressDialog(SplashActivity.this, mDialog);
        }

    }


}
