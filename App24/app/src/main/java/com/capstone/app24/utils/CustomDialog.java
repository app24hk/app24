package com.capstone.app24.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.capstone.app24.R;
import com.capstone.app24.activities.EditPostActivity;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by amritpal on 2/12/15.
 */
public class CustomDialog extends Dialog implements View.OnClickListener {
    private final String mMessage;
    Activity mActivity;
    int mDilogId;
    private TextView txt_message;
    private Button btn_ok, btn_post_comment;
    private EditText edit_comment;
    private String mPostId = null;
    private SweetAlertDialog mDilog;

    public CustomDialog(Context context, Activity activity, int dialogId, String message) {
        super(context);
        mActivity = activity;
        mDilogId = dialogId;
        mMessage = message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // setContentView(R.layout.dialog_class_session_selection);
        switch (mDilogId) {
            case Constants.SIMPLE_DIALOG:
                setContentView(R.layout.dialog_simple);
                setProperty();
                break;
            case Constants.FB_COMMENT_DIALOG:
                setContentView(R.layout.dialog_add_comment);
                setCommentProperty();
                break;
            default:
                break;
        }
    }

    public void setFacebookCommentDetails(String postId) {
        mPostId = postId;
    }


    private void setCommentProperty() {
        edit_comment = (EditText) findViewById(R.id.edit_write_comment);
        btn_post_comment = (Button) findViewById(R.id.btn_post_comment);
        btn_post_comment.setOnClickListener(this);
    }

    private void setProperty() {
        txt_message = (TextView) findViewById(R.id.txt_message);
        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                this.dismiss();
                break;
            case R.id.btn_post_comment:
                if (edit_comment
                        .getText().toString().trim().equalsIgnoreCase(Constants.EMPTY)) {
                    edit_comment.setError("Please add a comment");
                    return;
                }
                if (mPostId != null && !mPostId.equalsIgnoreCase(Constants.EMPTY)) {
                    mDilog = Utils.showSweetProgressDialog(mActivity, mActivity.getResources().getString(R.string
                            .please_wait), SweetAlertDialog.PROGRESS_TYPE);
                    postComment(edit_comment.getText().toString().trim());

                }
                break;
            default:
                break;
        }
    }

    private void postComment(String message) {
        dismiss();
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
                        Utils.closeSweetProgressDialog(mActivity, mDilog);
                        Utils.debug(Constants.FACEBOOK, "response of Comments : " + response);
                    }
                }
        ).executeAsync();
    }
}
