package com.clemSP.iteration1.frontend.dataset_management;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.Window;


public abstract class DatasetTask extends AsyncTask<Void, String, Boolean>
{
    protected static final String DATASET_FILE = "dataset.arff";
    protected ProgressDialog mProgressDialog;
    private int mMessageRes;

    protected Activity mActivity;
    protected DatasetTaskListener mListener;

    protected String mUrl;


    public interface DatasetTaskListener
    {
        void onTaskCompleted(DatasetTask task, boolean status);
    }


    public DatasetTask(int dialogMessage, Activity activity, String url)
    {
        mMessageRes = dialogMessage;

        mActivity = activity;

        if(activity instanceof DatasetTaskListener)
            mListener = (DatasetTaskListener) activity;

        mUrl = url;
    }


    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

        mProgressDialog = new ProgressDialog(mActivity);

        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressDialog.setMessage(mActivity.getString(mMessageRes));
        mProgressDialog.setCancelable(false);

        mProgressDialog.show();
    }


    @Override
    protected void onPostExecute(Boolean status)
    {
        super.onPostExecute(status);

        mProgressDialog.dismiss();

        mListener.onTaskCompleted(this, status);
    }
}
