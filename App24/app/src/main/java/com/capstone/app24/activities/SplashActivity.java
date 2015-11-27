package com.capstone.app24.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.capstone.app24.R;
import com.capstone.app24.api_services.IApiMethods;
import com.capstone.app24.callback.MyAsyncTask;
import com.capstone.app24.utils.APIsConstants;
import com.capstone.app24.utils.AlertToastManager;
import com.capstone.app24.utils.Constants;
import com.capstone.app24.utils.Utils;
import com.capstone.app24.webservices_model.UserLoginModel;
import com.crittercism.app.Crittercism;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookDialog;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.Field;

/**
 * Created by amritpal on 2/11/15.
 */
public class SplashActivity extends Activity implements View.OnClickListener, MyAsyncTask.MyAsyncTaskListener {

    private static final String TAG = SplashActivity.class.getSimpleName();
    private static final String API_TAG = "WebServices";
    private TextView txt_terms_of_use;
    private Button btn_login_with_facebook;
    List<String> permissions = new ArrayList<String>();
    private LoginButton fb_btn;

    CallbackManager callbackManager;
    AccessToken accessToken;
    private RestAdapter mRestAdapter;
    private UserLoginModel userModel;
    private UserLoginModel mUserBeanResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Crittercism.initialize(getApplicationContext(), Constants.CRITTERCISM_APP_ID);
        FacebookSdk.sdkInitialize(this);
//        if (new Utils(this).getSharedPreferences(this, Constants.KEY_IS_LOGGED_IN)) {
//            finish();
//            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//            startActivity(intent);
//        }
        setContentView(R.layout.activity_splash);
        txt_terms_of_use = (TextView) findViewById(R.id.txt_terms_of_use);
        btn_login_with_facebook = (Button) findViewById(R.id.btn_login_with_facebook);
        callbackManager = CallbackManager.Factory.create();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        initialize();
        setClickListeners();
        updateUI();
    }

    private void initialize() {

        txt_terms_of_use = (TextView) findViewById(R.id.txt_terms_of_use);
        btn_login_with_facebook = (Button) findViewById(R.id.btn_login_with_facebook);

        fb_btn = (LoginButton) findViewById(R.id.login_button);
        getKeyHash();
        permissions.add("public_profile");
        permissions.add("email");
        permissions.add("user_birthday");
        //Facebook Button Initialization
        fb_btn.setReadPermissions(permissions);

        fb_btn.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        fb_btn.setCompoundDrawablePadding(0);
        fb_btn.setPadding(0, 0, 0, 0);
        fb_btn.setText("");
        fb_btn.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        fb_btn.setBackgroundResource(R.drawable.facebook);

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
                loginFacebook();
                //finish();
                // intent = new Intent(SplashActivity.this, MainActivity.class);
                //startActivity(intent);
                //finish();
                fb_btn.performClick();
                break;
            default:
                break;
        }
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
                        Log.d("accesstoken", loginResult.getAccessToken() + "");
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONObject object,
                                            GraphResponse response) {
                                        // Application code
                                        Log.d("Facebook User Detail ", response
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
                                                    first_name, last_name, gender, "android",
                                                    "djsfhjdbsbfsdbkjlsfd", "facebook", "", "");

                                            MyAsyncTask task = new MyAsyncTask();
                                            task.setListener(SplashActivity.this);
                                            task.execute();


                                        } catch (Exception e) {
                                            Log.d("error", e.getMessage());
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
                    }

                    @Override
                    public void onError(FacebookException e) {
                        AlertToastManager.showToast(getResources().getString(R.string
                                .error_occured), SplashActivity.this);
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

    @Override
    public void onPreExecuteConcluded() {
        Utils.debug(API_TAG, "Inside OnPreExecute");
        mRestAdapter = new RestAdapter.Builder()
                .setEndpoint(APIsConstants.API_BASE_URL)
                .build();
    }


    @Override
    public Object doInBackground(String... params) {
        IApiMethods methods = mRestAdapter.create(IApiMethods.class);
        Utils.debug(API_TAG, "Inside doInBackground");
        methods.getUserLoginModel(userModel.getUser_social_id(), userModel.getUser_email(),
                userModel.getUser_fname(), userModel.getUser_lname(), userModel.getUser_gender
                        (), userModel.getUser_deviceType(), userModel.getUser_deviceToken(),
                userModel.getUser_loginType(), new
                        Callback<UserLoginModel>() {

                            @Override
                            public void success(UserLoginModel languageBean, Response response) {
                                Utils.debug(API_TAG, "Inside success");
                                Utils.debug(API_TAG, "Inside success" + response);
                                Utils.debug(API_TAG, "Inside success" + languageBean);

                                mUserBeanResponse = new UserLoginModel();
                                mUserBeanResponse = languageBean;
                                setUserData();
                            }


                            @Override
                            public void failure(RetrofitError error) {
                                error.printStackTrace();
                            }
                        }
        );
        return null;
    }

    private void setUserData() {
        if (mUserBeanResponse != null) {
            Utils.debug(API_TAG, mUserBeanResponse.getUser_social_id() +
                    "\n " + "mUserBeanResponse.getUser_loginType()" + mUserBeanResponse.getUser_loginType() +
                    "\n " + "mUserBeanResponse.getUser_loginType()" + mUserBeanResponse.getUser_loginType() +
                    "\n" + "mUserBeanResponse.getUser_loginType()" + mUserBeanResponse.getUser_loginType() +
                    "\n" + "mUserBeanResponse.getUser_loginType()" + mUserBeanResponse.getUser_loginType() +
                    "\n" + "mUserBeanResponse.getUser_loginType()" + mUserBeanResponse.getUser_loginType() +
                    "\n" + "mUserBeanResponse.getUser_loginType()" + mUserBeanResponse.getUser_loginType() +
                    "\n" + "mUserBeanResponse.getUser_gender()" + mUserBeanResponse.getUser_gender() +
                    "\n" + "mUserBeanResponse.getUser_lname()" + mUserBeanResponse.getUser_lname() +
                    "\n" + "mUserBeanResponse.getUser_fname()" + mUserBeanResponse.getUser_fname() +
                    "\n" + "mUserBeanResponse.getResponse()" + mUserBeanResponse.getResponse() +
                    "\n" + "mUserBeanResponse.getUser_email()" + mUserBeanResponse.getUser_email());


            if (mUserBeanResponse.getResult().equalsIgnoreCase("true")) {
                new Utils(SplashActivity
                        .this).setPreferences
                        (SplashActivity.this, Constants
                                .KEY_IS_LOGGED_IN, true);
                finish();
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
                Utils.debug(API_TAG, mUserBeanResponse.getResponse());
            }
        } else {
            AlertToastManager.showToast("Error Occured", SplashActivity.this);
        }
    }

    @Override
    public void onPostExecuteConcluded(Object result) {

    }

}
