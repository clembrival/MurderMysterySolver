package com.clemSP.sourcecode.frontend.dataset_management;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.Window;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


public abstract class DatasetTask extends AsyncTask<Void, String, Integer>
{
    protected static final String DATASET_FILE = "dataset.arff";

    public static final int POSITIVE_RESULT = 1, NEGATIVE_RESULT = 0, ERROR = -1;

    protected ProgressDialog mProgressDialog;
    private int mMessageRes;

    protected Activity mActivity;
    protected SharedPreferences mSharedPref;
    protected DatasetTaskListener mListener;

    protected String mUrl;


    public interface DatasetTaskListener
    {
        void onTaskCompleted(DatasetTask task, int status);
    }


    public DatasetTask(int dialogMessage, Activity activity, String url)
    {
        mMessageRes = dialogMessage;

        mActivity = activity;

        if(activity instanceof DatasetTaskListener)
            mListener = (DatasetTaskListener) activity;

        mUrl = url;

        mSharedPref = PreferenceManager.getDefaultSharedPreferences(mActivity);
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
    protected void onPostExecute(Integer status)
    {
        super.onPostExecute(status);

        mProgressDialog.dismiss();

        mListener.onTaskCompleted(this, status);
    }


    protected HttpsURLConnection getConnection(boolean postRequest, String query, int contentLength)
            throws IOException
    {
        HttpsURLConnection connection = (HttpsURLConnection) new URL(mUrl + query).openConnection();
        connection.setReadTimeout(10000);
        connection.setConnectTimeout(15000);
        connection.setDoInput(true);

        if(postRequest)
        {
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Content-Length", "" + contentLength);
        }

        return connection;
    }


    protected String streamToString(InputStream stream, int length) throws IOException
    {
        Reader reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[length];

        reader.read(buffer);

        return new String(buffer);
    }
}
