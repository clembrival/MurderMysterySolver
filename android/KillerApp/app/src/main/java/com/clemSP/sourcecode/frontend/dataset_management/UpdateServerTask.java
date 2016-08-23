package com.clemSP.sourcecode.frontend.dataset_management;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import com.clemSP.sourcecode.R;
import com.clemSP.sourcecode.backend.AttributeFactory.AppAttribute;
import com.clemSP.sourcecode.backend.Data;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.net.ssl.HttpsURLConnection;


public class UpdateServerTask extends DatasetTask
{
    private static final String TAG = "UpdateServerTask";

    private Data mData;


    public UpdateServerTask(Activity activity, String url, Data data)
    {
        super(R.string.update_local, activity, url);

        mData = data;
    }


    @Override
    protected Integer doInBackground(Void... params)
    {
        String newTimestamp = sendDataToServer(getJsonData());

        if(newTimestamp == null)
            return ERROR;

        Log.w(TAG, "Sent the new data to the server.");

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString("timestamp", newTimestamp);
        editor.apply();

        return POSITIVE_RESULT;
    }


    private String getJsonData()
    {
        try
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("title", mData.getTitle());

            for(int index = 0; index < AppAttribute.getNumAttributes(); index++)
            {
                AppAttribute attribute = AppAttribute.getAttributeFromIndex(index);
                if(attribute != null)
                    jsonObject.put(attribute.getLabel(), mData.getAttributeValue(index+1));
            }
            Log.w(TAG, jsonObject.toString());

            return jsonObject.toString();
        }
        catch(JSONException jsone)
        {
            Log.e(TAG, Arrays.toString(jsone.getStackTrace()));
            return null;
        }
    }


    private String sendDataToServer(String jsonData)
    {
        if(jsonData == null)
            return null;

        HttpsURLConnection connection = null;
        DataOutputStream outputStream = null;
        InputStream inputStream = null;

        // Maximum number of characters to read
        final int BUFFER_LENGTH = 19;

        try
        {
            try
            {
                connection = getConnection(true, "", jsonData.length());

                connection.connect();

                outputStream = new DataOutputStream(connection.getOutputStream());
                outputStream.write(jsonData.getBytes());
                outputStream.flush ();

                inputStream = connection.getInputStream();

                return streamToString(inputStream, BUFFER_LENGTH);
            }
            finally
            {
                if(inputStream != null) inputStream.close();
                if(outputStream != null) outputStream.close();
                if(connection != null) connection.disconnect();
            }
        }
        catch(IOException ioe)
        {
            Log.e(TAG, ioe.getMessage());
        }
        return null;
    }
}
