package com.capstone.app24.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.capstone.app24.R;
import com.capstone.app24.utils.APIsConstants;
import com.capstone.app24.utils.AlertToastManager;
import com.capstone.app24.utils.AppController;
import com.capstone.app24.utils.Constants;
import com.capstone.app24.utils.NetworkUtils;
import com.capstone.app24.utils.Utils;
import com.capstone.app24.webservices_model.UserLoginModel;
import com.capstone.app24.webservices_model.UserLoginResponseModel;
import com.crittercism.app.Crittercism;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
    List<String> permissions = new ArrayList<String>();
    private LoginButton fb_btn;

    CallbackManager callbackManager;
    AccessToken accessToken;
    //private RestAdapter mRestAdapter;
    private UserLoginModel userModel;
    private UserLoginResponseModel mUserBeanResponse;
    private SweetAlertDialog mDialog;

    /* Volley Request Tags */
    private String res = "";
    private String tag_string_req = "feeds_req";
    /* End of Volley Request Tags */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Crittercism.initialize(getApplicationContext(), Constants.CRITTERCISM_APP_ID);
        //.............Facebook Integartion...............
        FacebookSdk.sdkInitialize(getApplicationContext());
        new Utils(this).setPreferences(this, Constants.FETCH_GALLERY_IMAGE, true);
        new Utils(this).setPreferences(this, Constants.FETCH_GALLERY_VIDEO, true);
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_splash);
        if (new Utils(this).getSharedPreferences(this, Constants.KEY_IS_LOGGED_IN, false)) {
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
        //LoginManager.getInstance().logOut();
    }

    private void initialize() {

        txt_terms_of_use = (TextView) findViewById(R.id.txt_terms_of_use);
        btn_login_with_facebook = (Button) findViewById(R.id.btn_login_with_facebook);

        fb_btn = (LoginButton) findViewById(R.id.login_button);
        getKeyHash();
        permissions.add("public_profile");
        permissions.add("email");
        permissions.add("user_birthday");
        permissions.add("publish_actions");

      /*  //Facebook Button Initialization
        fb_btn.setReadPermissions(permissions);


        fb_btn.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        fb_btn.setCompoundDrawablePadding(0);
        fb_btn.setPadding(0, 0, 0, 0);
        fb_btn.setText("");
        fb_btn.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        fb_btn.setBackgroundResource(R.drawable.facebook);*/


        FacebookIntegration();


    }

    private void setClickListeners() {
        txt_terms_of_use.setOnClickListener(this);
        btn_login_with_facebook.setOnClickListener(this);
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
             /*   mDialog = Utils.showSweetProgressDialog(this, getResources().getString(R.string
                        .progress_loading), SweetAlertDialog.PROGRESS_TYPE);
                loginFacebook();*/
                //finish();
                // intent = new Intent(SplashActivity.this, MainActivity.class);
                //startActivity(intent);
                //finish();
                final SweetAlertDialog pd = Utils.showSweetProgressDialog(SplashActivity.this,
                        getResources
                                ().getString(R.string.progress_loading), SweetAlertDialog.PROGRESS_TYPE);
                fb_btn.performClick();
                break;
            default:
                break;
        }
    }

    //------------------------------Facebook SignIn or SignUp---------------------
    public void FacebookIntegration() {
        fb_btn.setReadPermissions(Arrays.asList("public_profile, email, user_birthday, " +
                "user_friends"));
        fb_btn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                // Application code
                                Log.v("Facebook User Detail : ", response.toString());
                                Log.v("Facebook  Detail : ", response.getJSONObject().toString());
//                                if(!response.toString().isEmpty()){
                                try {
                                    JSONObject jsonObject = response.getJSONObject();

                                    String email = "", username, pictureobj, url, facebookid = "", last_name = "",
                                            first_name = "", gender = "";


                                    Log.v("Facebook  Detail sec: ", jsonObject.toString());
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
                                    userModel = new UserLoginModel(facebookid, email,
                                            first_name, last_name, gender, Constants
                                            .ANDROID,
                                            accessToken + "", Constants.FACEBOOK);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }

//                                            MyAsyncTask task = new MyAsyncTask();
//                                            task.setListener(SplashActivity.this);
//                                            task.execute();
                                makeUserLoginRequest();
                                if (NetworkUtils.isOnline(SplashActivity.this)) {
                                    makeUserLoginRequest();
                                } else {
                                    Utils.showSweetProgressDialog(SplashActivity.this,
                                            getResources().getString(R.string
                                                    .check_your_internet_connection),
                                            SweetAlertDialog.ERROR_TYPE);
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
                AlertToastManager.showToast(getResources().getString(R.string.please_try_again),
                        SplashActivity
                                .this);
                Utils.closeSweetProgressDialog(SplashActivity.this, mDialog);
            }

            @Override
            public void onError(FacebookException e) {
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

    //Facebook Login
    private void loginFacebook() {

        fb_btn.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        accessToken = loginResult.getAccessToken();
                        btn_login_with_facebook.setOnClickListener(null);
                        Utils.debug("accesstoken", loginResult.getAccessToken() + "");
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONObject object,
                                            GraphResponse response) {
                                        // Application code
                                        Utils.debug("Facebook User Detail ", response
                                                .getJSONObject().toString());
                                        try {
                                            JSONObject obj = response.getJSONObject();
                                            String email = obj.getString(Constants.KEY_EMAIL);

                                            String username = obj.getString(Constants.KEY_NAME);
                                            JSONObject pictureobj = obj.getJSONObject(Constants.KEY_PICTURE)
                                                    .getJSONObject(Constants.KEY_DATA);
                                            String url = pictureobj.getString(Constants.KEY_URL);

                                            String facebookid = obj.getString(Constants.KEY_ID);
                                            String last_name = obj.getString(Constants.KEY_LAST_NAME);
                                            String first_name = obj.getString(Constants
                                                    .KEY_FIRST_NAME);
                                            String gender = obj.getString(Constants.KEY_GENDER);
                                            userModel = new UserLoginModel(facebookid, email,
                                                    first_name, last_name, gender, Constants
                                                    .ANDROID,
                                                    accessToken + "", Constants.FACEBOOK);
//                                            MyAsyncTask task = new MyAsyncTask();
//                                            task.setListener(SplashActivity.this);
//                                            task.execute();
                                            makeUserLoginRequest();

                                        } catch (Exception e) {
                                            Utils.debug("error", e.getMessage());
                                            e.printStackTrace();
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
                        AlertToastManager.showToast(getResources().getString(R.string.please_try_again),
                                SplashActivity
                                        .this);
                        Utils.closeSweetProgressDialog(SplashActivity.this, mDialog);
                    }

                    @Override
                    public void onError(FacebookException e) {
                        AlertToastManager.showToast(getResources().getString(R.string
                                .error_occured), SplashActivity.this);
                        Utils.closeSweetProgressDialog(SplashActivity.this, mDialog);
                    }
                });
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

//    @Override
//    public void onPreExecuteConcluded() {
//        mDialog = Utils.showSweetProgressDialog(SplashActivity.this, "Loading...");
////        mRestAdapter = new RestAdapter.Builder()
////                .setEndpoint(APIsConstants.API_BASE_URL)
////                .build();
//    }


//    @Override
//    public Object doInBackground(String... params) {
//        //   IApiMethods methods = mRestAdapter.create(IApiMethods.class);
////        methods.getUserLoginModel(userModel.getUser_social_id(), userModel.getUser_email(),
////                userModel.getUser_fname(), userModel.getUser_lname(), userModel.getUser_gender
////                        (), userModel.getUser_deviceType(), userModel.getUser_deviceToken(),
////                userModel.getUser_loginType(), new
////                        Callback<UserLoginResponseModel>() {
////                            @Override
////                            public void success(UserLoginResponseModel userLoginResponseModel, Response response) {
////
////                                mUserBeanResponse = new UserLoginResponseModel();
////                                mUserBeanResponse = userLoginResponseModel;
////                                if (mUserBeanResponse.isResult()) {
////                                    UserLoginResponseModel.UserInfo userInfo = mUserBeanResponse
////                                            .getUserInfo();
////                                    Utils.debug(Constants.API_TAG, "UserInfo : { " + userInfo
////                                            .getUser_id() + ", " + userInfo.getUser_email() + ", " +
////                                            "" + userInfo.getUser_fname() + ", " + userInfo
////                                            .getUser_lname() + ", " + userInfo.getUser_gender() +
////                                            ", " + userInfo.getUser_loginType() + ", " + userInfo
////                                            .getUser_social_id() + " }");
////                                    mUserInfo = mUserBeanResponse
////                                            .getUserInfo();
////                                    setUserData();
////                                } else {
////
////                                    Utils.debug(Constants.API_TAG, mUserBeanResponse.getMessage());
////                                    btn_login_with_facebook.setOnClickListener(SplashActivity.this);
////                                }
////                            }
////
////                            @Override
////                            public void failure(RetrofitError error) {
////                                error.printStackTrace();
////                            }
////                        }
////        );
//        return null;
//    }


//    @Override
//    public void onPostExecuteConcluded(Object result) {
//
//    }


    //...............Volley String Request..............

    public boolean makeUserLoginRequest() {
        /*
         Starting a progress Dialog...
         If second parameter is passed null then progressdialog will show (Loading...) by default if pass string such as(Searching..) then
         it will show (Searching...)
         */
        Utils.debug(TAG, "Inside On makeLogin");

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
                            // TODO Auto-generated catch block
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
//                        user_id(int)
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_social_id", userModel.getUser_social_id());
                params.put("user_email", userModel.getUser_email());
                params.put("user_fname", userModel.getUser_fname());
                params.put("user_lname", userModel.getUser_lname());
                params.put("user_gender", userModel.getUser_gender());
                params.put("user_deviceType", userModel.getUser_deviceType());
                params.put("user_deviceToken", userModel.getUser_deviceToken());
                params.put("user_loginType", userModel.getUser_deviceType());
                Utils.info("params...", params.toString());
                return params;
            }
            // Adding request to request queue
        };
        AppController.getInstance().addToRequestQueue(strReq, Constants.ADD_TO_QUEUE);
        return false;
    }

    private void setUserData(String res) throws JSONException {
//        {
//            "result": true,
//                "userInfo": {
//                    "user_id": "1",
//                    "user_email": "er.amrit13@gmail.com",
//                    "user_fname": "Er",
//                    "user_lname": "Amrit",
//                    "user_gender": "male",
//                    "user_name": "Er Amrit",
//                    "user_loginType": "facebook",
//                    "user_social_id": "994613123922505"
//        }
//        }
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
//                mUserBeanResponse.setUser_name(userInfoObject.getString(APIsConstants.KEY_USER_NAME));
                mUserBeanResponse.setUser_name(userInfoObject.getString(APIsConstants.KEY_USER_FNAME));

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
                new Utils().setPreferences(SplashActivity.this, Constants.KEY_FACEBOOK_ACCESS_TOKEN,
                        accessToken + "");

                Utils.debug("acc", new Utils().getSharedPreferences(SplashActivity.this, Constants
                        .KEY_FACEBOOK_ACCESS_TOKEN, Constants.EMPTY));
                Utils.closeSweetProgressDialog(SplashActivity.this, mDialog);
                finish();
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
            }

        } else {
            try {
                Utils.debug(Constants.API_TAG, jsonObject.getString(APIsConstants.KEY_MESSAGE));
                Utils.showSweetProgressDialog(SplashActivity.this, jsonObject.getString(APIsConstants
                        .KEY_MESSAGE), SweetAlertDialog.ERROR_TYPE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Utils.closeSweetProgressDialog(SplashActivity.this, mDialog);
    }

    private void checkHomeFeedResponse(String res) throws JSONException {
        Utils.debug(TAG, "Response : " + res);
//        {
//            "user_info": {
//                    "result": "true",
//                    "response": "Thank You for Registering! A confirmation email with activation link in sent to the email address.",
//                    "user_id": 32
//        }
//        }

//        JSONObject jo = null, jo1 = null;
//        JSONArray ja;
//
//        String result = "false";
//        String response = "";
//        Log.d("res register", res.toString());
//
//
//        jo1 = new JSONObject(res);
//
//        jo = jo1.getJSONObject("user_info");
//
//        if (jo.has("result")) {
//            result = jo.getString("result");
//        }
//
//        if (jo.has("response")) {
//            response = jo.getString("response");
//        }
//
//
//        if (result.equals("true")) {
//
//
//            if (jo.has("data")) {
//                ja = jo.getJSONArray("data");
//
//                list_homefeeds.clear();
//
//                for (int i = 0; i < ja.length(); i++) {
//                    JSONObject join = ja.getJSONObject(i);
//                    HomeFeedModel homeFeedModel = new HomeFeedModel();
//
//                    if (join.has("coupon_id")) {
//                        homeFeedModel.setCoupon_id(join.getString("coupon_id"));
//                    }
//                    if (join.has("title")) {
//                        homeFeedModel.setTitle(join.getString("title"));
//                    }
//                    if (join.has("description")) {
//                        homeFeedModel.setDescription(join.getString("description"));
//                    }
//                    if (join.has("favorite")) {
//                        homeFeedModel.setFavorite(join.getString("favorite"));
//                    }
//                    if (join.has("location")) {
//                        homeFeedModel.setLocation(join.getString("location"));
//                    }
//                    if (join.has("image")) {
//                        homeFeedModel.setImage(join.getString("image"));
//                    }
//                    if (join.has("icons")) {
//                        JSONArray jaicon = join.getJSONArray("icons");
//                        homeFeedModel.setIcon(jaicon.getString(0).toString().trim());
//                    }
//                    list_homefeeds.add(homeFeedModel);
//                }
//                adapter.notifyDataSetChanged();
//            }
//
//        } else if (result.equals("false")) {
//            CommonControl.showSweetErrorDialog(context, "Error", response);
//        }
    }


}
