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
import com.capstone.app24.utils.AlertToastManager;
import com.capstone.app24.utils.Constants;
import com.capstone.app24.utils.Utils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        /*if (new Utils(this).getSharedPreferences(this, Constants.KEY_IS_LOGGED_IN)) {
            finish();
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
        }*/
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
                Utils.debug(TAG, "Start Activity Main");
                //finish();
//                intent = new Intent(SplashActivity.this, MainActivity.class);
//                startActivity(intent);
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

    //facebook Login
    private void loginFacebook() {

        fb_btn.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        accessToken = loginResult.getAccessToken();
                        Log.d("accesstoken", loginResult.getAccessToken() + "");
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONObject object,
                                            GraphResponse response) {
                                        // Application code
                                        Log.d("Facebook User Detail >>########: ", response
                                                .getJSONObject().toString());
                                        try {
                                            JSONObject obj = response.getJSONObject();
                                            String email = obj.getString("email");

                                            String username = obj.getString("name");
                                            JSONObject pictureobj = obj.getJSONObject("picture").getJSONObject("data");
                                            String url = pictureobj.getString("url");

                                            String facebookid = obj.getString("id");
                                            String last_name = obj.getString("last_name");
                                            Utils.debug("email", " " + email);
                                            Utils.debug("url", " " + url);
                                            Utils.debug("username", " " + username);
                                            Utils.debug("facebookid", " " + facebookid);
                                            Utils.debug("last_name", " " + last_name);

                                            new Utils(SplashActivity.this).setPreferences
                                                    (SplashActivity.this, Constants
                                                            .KEY_IS_LOGGED_IN, true);


                                            finish();
                                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                            startActivity(intent);
                                        } catch (Exception e) {
                                            Log.d("error", e.getMessage());
                                            e.printStackTrace();
                                        }


                                    }
                                });
                        Bundle parameters = new Bundle();
//                @"email,name,first_name,last_name,gender,picture,albums"}];
                        parameters.putString("fields", "id,name,first_name,last_name,email,gender, birthday,picture");
                        request.setParameters(parameters);
                        request.executeAsync();
                       /* Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);*/
                        // displayProfile();
                    }

                    @Override
                    public void onCancel() {
                        AlertToastManager.showToast("Please Try Again", SplashActivity.this);
                    }

                    @Override
                    public void onError(FacebookException e) {
                        AlertToastManager.showToast("Error Occured", SplashActivity.this);
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
}
