package com.capstone.app24.utils;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by amritpal on 4/1/16.
 */
public class FacebookUtils {
    private static String mUrl;

    public static String getFeedUrl(String feedId) {
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + feedId,
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
            /* handle the result */
                        Utils.debug(Constants.FACEBOOK, "response.getRawResponse() :  " +
                                "" + response.getRawResponse());
                        Utils.debug(Constants.FACEBOOK, "response.getRawResponse() :  " +
                                "" + response.getJSONObject());
                        JSONObject object = response.getJSONObject();
                        JSONObject jsonObject = null;
                        if (object != null) {
                            try {
                                jsonObject = object.getJSONObject(Constants.KEY_DATA);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        JSONObject urlObject1 = null;
                        if (jsonObject != null) {
                            try {
                                urlObject1 = jsonObject.getJSONObject(Constants.POST);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        if (urlObject1 != null) {
                            try {
                                String url = urlObject1.getString(Constants.KEY_URL);
                                Utils.debug(Constants.FACEBOOK, "url : " + url);
                                mUrl = url;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        ).executeAsync();
        return mUrl;
    }
}
