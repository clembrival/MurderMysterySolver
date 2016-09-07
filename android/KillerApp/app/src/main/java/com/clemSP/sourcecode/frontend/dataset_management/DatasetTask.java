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


/** 
  * This class contains all the methods shared by all the AsyncTask classes. */
public abstract class DatasetTask extends AsyncTask<Void, String, Integer>
{
    /** Status codes returned at the end of the execution of the task. */
    public static final int POSITIVE_RESULT = 1, NEGATIVE_RESULT = 0, ERROR = -1;

    /** Dialog showing the progress of the task. */
    protected ProgressDialog mProgressDialog;
    /** Resource containing the message to be printed on the dialog. */
    private int mMessageRes;

    /** Activity requesting the task. */
    protected Activity mActivity;
    /** Reference to the class storing the preferences shared by the activities. */
    protected SharedPreferences mSharedPref;
    /** Listener object to be notified at the end of the task. */
    protected DatasetTaskListener mListener;

    /** Url to be accessed during the task. */
    protected String mUrl;


    /** Interface implemented by the class requesting the task. */
    public interface DatasetTaskListener
    {
        /**
          * Notifies the listener at the end of the task.
          * @param task the task which just ended
          * @param status the status with which the task ended
          */
        void onTaskCompleted(DatasetTask task, int status);
    }


    /**
      * Constructor
      * @param the resource of the string storing the message to be displayed
      *         during the task
      * @param activity the activity requesting the task
      * @param the url to be accessed during the task
      */
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

        // Open dialog at the start of the task
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

        // Close the dialog at the end of the task
        mProgressDialog.dismiss();

        // Notify the listener
        mListener.onTaskCompleted(this, status);
    }


    /**
      * Opens an https url connection to the url stored in the object.
      * @param postRequest whether the request is a POST (true) or a GET (false)
      * @param query the query to be passed in the url
      * @param contentLength the length of the content of the request
      * @return the opened https url connection
      */
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


    /**
      * Converts a given input stream to a string.
      * @param stream the stream to be converted
      * @param length the number of characters to be extracted from the stream
      */
    protected String streamToString(InputStream stream, int length) throws IOException
    {
        Reader reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[length];

        reader.read(buffer);

        return new String(buffer);
    }
}
