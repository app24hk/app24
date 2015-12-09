package com.capstone.app24.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.capstone.app24.R;

/**
 * Created by amritpal on 2/12/15.
 */
public class CustomDialog extends Dialog implements View.OnClickListener {
    private final String mMessage;
    Activity mActivity;
    int mDilogId;
    private TextView txt_message;
    private Button btn_ok;

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
            default:
                break;
        }
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
            default:
                break;
        }
    }
}
