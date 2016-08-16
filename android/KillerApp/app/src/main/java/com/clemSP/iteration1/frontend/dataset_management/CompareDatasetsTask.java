package com.clemSP.iteration1.frontend.dataset_management;

import android.app.Activity;
import android.util.Log;

import com.clemSP.iteration1.R;

import java.io.InputStream;

import javax.net.ssl.HttpsURLConnection;


/** Class implementing a background task to compare the date and time the local dataset
  * was last updated to the timestamp of the server's last entry. */
public class CompareDatasetsTask extends DatasetTask
{
    private static final String TAG = "CompareDatasetsTask";


    public CompareDatasetsTask(Activity activity, String url)
    {
        super(R.string.compare_datasets, activity, url);
    }


    @Override
    protected Integer doInBackground(Void... params)
    {
        String localTimestamp = getLocalTimestamp();
        Log.w(TAG, "Local time: " + localTimestamp);

        String serverTimestamp = getServerTimestamp();
        Log.w(TAG, "Server time: " + serverTimestamp);

        if(serverTimestamp == null)
            return ERROR;

        if(localTimestamp.equals("") || localTimestamp.compareTo(serverTimestamp) < 0)
            return POSITIVE_RESULT;

        return NEGATIVE_RESULT;
    }


    /**
     * @return the timestamp of the last update of the local dataset, if any.
     */
    private String getLocalTimestamp()
    {
        return mSharedPref.getString("timestamp", "");
    }


    /**
     * Connects to the killerapp/timestamp url of the server.
     * @return the timestamp of the server's last entry
     */
    private String getServerTimestamp()
    {
        HttpsURLConnection connection = null;
        InputStream stream = null;

        // Maximum number of characters to read
        final int BUFFER_LENGTH = 19;

        try
        {
            try
            {
                connection = getConnection(false, "", 0);

                connection.connect();

                stream = connection.getInputStream();

                return streamToString(stream, BUFFER_LENGTH);
            }
            finally
            {
                if(stream != null) stream.close();
                if(connection != null) connection.disconnect();
            }
        }
        catch(Exception e)
        {
            Log.e(TAG, e.getLocalizedMessage());
        }
        return null;
    }
}