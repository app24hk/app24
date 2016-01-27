package com.capstone.app24.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.capstone.app24.R;
import com.capstone.app24.adapters.CommentAdapter;
import com.capstone.app24.bean.CommentModel;
import com.capstone.app24.utils.APIsConstants;
import com.capstone.app24.utils.Constants;
import com.capstone.app24.utils.Utils;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.apache.commons.lang.StringEscapeUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by amritpal on 14/1/16.
 */
public class CommentActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = CommentActivity.class.getSimpleName();
    private ImageView profile_pic;
    private ListView list_comment;
    private String mPostId;
    private List<CommentModel> mCommentList = new ArrayList<>();
    private CommentAdapter mCommentAdapter;
    private EditText edit_write_comment;
    private SweetAlertDialog mDialog;
    private Button post_comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        setHeader(null, true, false, false, false, false, null);
        initializeViews();
        setClickListeners();
        updateUI();
    }

    /**
     * Initializing views for user interface
     */
    private void initializeViews() {
        profile_pic = (ImageView) findViewById(R.id.profile_pic);
        list_comment = (ListView) findViewById(R.id.list_comment);
        edit_write_comment = (EditText) findViewById(R.id.edit_write_comment);
        post_comment = (Button) findViewById(R.id.post_comment);
    }

    /**
     * Setting click listeners
     */
    private void setClickListeners() {
        post_comment.setOnClickListener(this);
        edit_write_comment.setImeActionLabel("Done", EditorInfo.IME_ACTION_DONE);
        edit_write_comment.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //setPaypalDetails();
                    postComment(StringEscapeUtils.escapeJava(edit_write_comment.getText()
                            .toString().trim()));
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    private void postComment(String message) {
//        if (mDialog == null)
//            mDialog = Utils.showSweetProgressDialog(CommentActivity.this, CommentActivity.this.getResources()
//                    .getString(R
//                            .string.please_wait), SweetAlertDialog.PROGRESS_TYPE);
        //dismiss();
        Bundle params = new Bundle();
        params.putString("message", message);
/* make the API call */
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + mPostId + "/comments",
                params,
                HttpMethod.POST,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
            /* handle the result */
                        if (response != null) {
                            Utils.closeSweetProgressDialog(CommentActivity.this, mDialog);
                            Utils.debug(Constants.FACEBOOK, "response of Comments : " + response);
                            Utils.debug(TAG, response.getRawResponse() + "");
                            String s = response.getRawResponse();
                            try {
                                if (s != null) {
                                    JSONObject object = new JSONObject(s);
                                    if (object != null) {
                                        String id = object.getString(Constants.KEY_ID);
                                        if (id != null && !id.equalsIgnoreCase(Constants.EMPTY)) {
                                            // getComments(mPostId);
                                            edit_write_comment.setText("");
                                            edit_write_comment.setHint(getResources().getString(R
                                                    .string.write_a_comment));
                                            getCommentList();
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                if (mDialog != null)
                                    Utils.closeSweetProgressDialog(CommentActivity.this, mDialog);
                            }

//                            Utils.debug(TAG, response.getRawResponse());
                            //  JSONObject graphResponse = response.getJSONObject();
                            // Utils.debug(TAG, "graphResponse : " + graphResponse);
                        }
                    }
                }
        ).executeAsync();
    }

    /**
     * Updating UI with data
     */
    private void updateUI() {
        Intent intent = getIntent();
        if (intent.hasExtra(Constants.POST_ID))
            mPostId = intent.getStringExtra(Constants.POST_ID);

        Utils.debug(TAG, new Utils(this).getSharedPreferences(this, Constants
                .FB_IMAGE_SMALL, ""));

        Glide.with(this).load(Uri.parse(new Utils(this).getSharedPreferences(this, Constants
                .FB_IMAGE_SMALL, ""))).centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL).crossFade()
                .into(profile_pic);
        getCommentList();


        mCommentAdapter = new CommentAdapter(CommentActivity.this, mCommentList);
        list_comment.setAdapter(mCommentAdapter);
    }

    private void getCommentList() {
    /* make the API call */
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + mPostId + "/comments",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
            /* handle the result */

                        Utils.debug(Constants.FACEBOOK, response.toString());
                        if (response != null) {
                            JSONObject graphResponse = response.getJSONObject();
                            Utils.debug(TAG, "graphResponse : " + graphResponse);
                            if (graphResponse != null) {
                                try {
                                    JSONArray jsonArray = graphResponse.getJSONArray(Constants.KEY_DATA);
                                    Utils.debug(TAG, "List of comments array " + jsonArray);
                                    mCommentList.clear();
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject object = jsonArray.getJSONObject(i);
                                        CommentModel commentModel = new CommentModel();
                                        commentModel.setCreated_time(object.getString(APIsConstants
                                                .CREATED_TIME));
                                        commentModel.setMessage(StringEscapeUtils.unescapeJava
                                                (object.getString(APIsConstants.KEY_MESSAGE)));
                                        commentModel.setComment_id(object.getString(APIsConstants.KEY_ID));
                                        JSONObject fromObject = object.getJSONObject(APIsConstants.KEY_FROM);
                                        commentModel.setName(fromObject.getString(APIsConstants.NAME));
                                        commentModel.setId(fromObject.getString(APIsConstants.KEY_ID));
                                        if (!mCommentList.contains(commentModel)) {
                                            mCommentList.add(commentModel);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        Utils.closeSweetProgressDialog(CommentActivity.this, mDialog);
                        Collections.reverse(mCommentList);
                        mCommentAdapter.notifyDataSetChanged();
                    }
                }
        ).executeAsync();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.post_comment:
                //postComment(edit_write_comment.getText().toString().trim());
                if (edit_write_comment
                        .getText().toString().trim().equalsIgnoreCase(Constants.EMPTY)) {
                    edit_write_comment.setError(getResources().getString(R.string
                            .please_add_a_comment));
                    return;
                }
                if (mPostId != null && !mPostId.equalsIgnoreCase(Constants.EMPTY)) {
                    mDialog = Utils.showSweetProgressDialog(CommentActivity.this, CommentActivity.this
                            .getResources()
                            .getString(R.string
                                    .please_wait), SweetAlertDialog.PROGRESS_TYPE);
                    postComment(edit_write_comment.getText().toString().trim());

                }
                break;
            case R.id.ibtn_back:
                finish();
                break;
        }
    }
}
