package com.capstone.hk.callback;

import android.os.AsyncTask;

/**
 * Created by amritpal on 25/11/15.
 */
public class MyAsyncTask extends AsyncTask<String, Void, Object> {

    public interface MyAsyncTaskListener {
        void onPreExecuteConcluded();

        void onPostExecuteConcluded(Object result);

        Object doInBackground(String... params);
    }

    private MyAsyncTaskListener mListener;

    final public void setListener(MyAsyncTaskListener listener) {
        mListener = listener;
    }

    @Override
    protected Object doInBackground(String... progress) {
        try {
            if (mListener != null)
                mListener.doInBackground(progress);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    final protected void onPreExecute() {
        try {
            if (mListener != null)
                mListener.onPreExecuteConcluded();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    final protected void onPostExecute(Object result) {
        try {
            if (mListener != null)
                mListener.onPostExecuteConcluded(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}