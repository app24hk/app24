package app24.feedbook.hk.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import app24.feedbook.hk.R;
import app24.feedbook.hk.activities.PostDetailActivity;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by amritpal on 2/12/15.
 */
public class CustomDialog extends Dialog implements View.OnClickListener {
    private static final String TAG = CustomDialog.class.getSimpleName();
    private final String mMessage;
    Activity mActivity;
    int mDilogId;
    private TextView txt_message;
    private Button btn_ok, btn_post_comment;
    private EditText edit_comment;
    private String mPostId = null;
    private SweetAlertDialog mDilog;
    private SweetAlertDialog mDialog;

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
//            case Constants.FB_COMMENT_DIALOG:
//                setContentView(R.layout.dialog_add_comment);
//                setCommentProperty();
//                break;
            default:
                break;
        }
    }

    public void setFacebookCommentDetails(String postId) {
        mPostId = postId;
    }


    private void setCommentProperty() {
        edit_comment = (EditText) findViewById(R.id.edit_write_comment);
        // btn_post_comment = (Button) findViewById(R.id.btn_post_comment);
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
//            case R.id.btn_post_comment:
//                if (edit_comment
//                        .getText().toString().trim().equalsIgnoreCase(Constants.EMPTY)) {
//                    edit_comment.setError("Please add a comment");
//                    return;
//                }
//                if (mPostId != null && !mPostId.equalsIgnoreCase(Constants.EMPTY)) {
//                    mDilog = Utils.showSweetProgressDialog(mActivity, mActivity.getResources().getString(R.string
//                            .please_wait), SweetAlertDialog.PROGRESS_TYPE);
//                    postComment(edit_comment.getText().toString().trim());
//
//                }
//                break;
            default:
                break;
        }
    }

    private void postComment(String message) {
        if (mDilog == null)
            mDialog = Utils.showSweetProgressDialog(mActivity, mActivity.getResources()
                    .getString(R
                            .string.please_wait), SweetAlertDialog.PROGRESS_TYPE);
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
                        Utils.debug(TAG, response.getRawResponse() + "");
                        String s = response.getRawResponse();
                        try {
                            JSONObject object = new JSONObject(s);
                            String id = object.getString(Constants.KEY_ID);
                            if (id != null && !id.equalsIgnoreCase(Constants.EMPTY)) {
                                getComments(mPostId);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            if (mDilog != null)
                                Utils.closeSweetProgressDialog(mActivity, mDialog);
                        }

                        Utils.debug(TAG, response.getRawResponse());
                        JSONObject graphResponse = response.getJSONObject();
                        Utils.debug(TAG, "graphResponse : " + graphResponse);
                    }
                }
        ).executeAsync();
    }

    private void getComments(final String postId) {
    /* make the API call */
        if (mDilog == null)
            mDilog = Utils.showSweetProgressDialog(mActivity, mActivity.getResources().getString(R.string
                    .please_wait), SweetAlertDialog.PROGRESS_TYPE);
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + postId + "/comments",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
            /* handle the result */

                        Utils.debug(Constants.FACEBOOK, response.toString());

                        JSONObject graphResponse = response.getJSONObject();
                        Utils.debug(TAG, "graphResponse : " + graphResponse);
                        try {
                            JSONArray jsonArray = graphResponse.getJSONArray(Constants.KEY_DATA);
                            Utils.debug(TAG, "jsonArray : " + jsonArray);
                            PostDetailActivity.commentCount = jsonArray.length();
                            if (mDilog != null)
                                Utils.closeSweetProgressDialog(mActivity, mDialog);
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (mDilog != null)
                                Utils.closeSweetProgressDialog(mActivity, mDialog);
                        }
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                PostDetailActivity.txt_comment.setText(PostDetailActivity
                                        .commentCount + "");
                            }
                        });
//                        txt_comment.setText(commentCount + "");
                    }
                }
        ).executeAsync();
    }

}
