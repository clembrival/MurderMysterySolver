package com.clemSP.iteration1.frontend.dataset_management;

import android.app.Activity;
import android.renderscript.ScriptGroup;
import android.util.Log;

import com.clemSP.iteration1.R;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Arrays;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;


/** Class implementing a background task to compare the number of instances in the local dataset
  * and the server's dataset. */
public class CompareDatasetsTask extends DatasetTask
{
    private static final String TAG = "UpdateDatasetActivity";


    public CompareDatasetsTask(Activity activity, String url)
    {
        super(R.string.local_length, activity, url);
    }


    @Override
    protected Boolean doInBackground(Void... params)
    {
        int localLength = getLocalLength();
        Log.w(TAG, "Local length: " + localLength);

        publishProgress(mActivity.getString(R.string.server_length));

        int serverLength = getServerLength();
        Log.w(TAG, "Server length: " + serverLength);

        return localLength != 0 && serverLength != 0 && localLength < serverLength;
    }


    @Override
    protected void onProgressUpdate(String... values)
    {
        super.onProgressUpdate(values);

        mProgressDialog.setMessage(values[0]);
    }


    private int getLocalLength()
    {
        final int HEADER_LINES = 18;

        int lines = 0;
        FileReader reader = null;
        Scanner scanner = null;

        try
        {
            try
            {
                File file = mActivity.getBaseContext().getFileStreamPath(DATASET_FILE);

                reader = new FileReader(file);
                scanner = new Scanner(reader);

                while (scanner.hasNextLine())
                {
                    lines++;
                    scanner.nextLine();
                }

                lines -= HEADER_LINES;
            }
            finally
            {
                if(reader != null)
                    reader.close();
                if(scanner != null)
                    scanner.close();
            }
        }
        catch (IOException ioe)
        {
            Log.e(TAG, Arrays.toString(ioe.getStackTrace()));
        }

        return lines;
    }


    private int getServerLength()
    {
        HttpsURLConnection connection = null;
        InputStream stream = null;

        final int BUFFER_LENGTH = 5;

        try
        {
            try
            {
                connection = (HttpsURLConnection) new URL(mUrl).openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);

                connection.connect();

                stream = connection.getInputStream();

                return streamToInt(stream, BUFFER_LENGTH);
            }
            finally
            {
                if(stream != null)
                    stream.close();
                if(connection != null)
                    connection.disconnect();
            }
        }
        catch(Exception e)
        {
            Log.e(TAG, Arrays.toString(e.getStackTrace()));
        }
        return 0;
    }




    /** Reads an InputStream and converts it to a String. */
    private int streamToInt(InputStream stream, int length) throws IOException
    {
        Reader reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[length];

        reader.read(buffer);

        return Integer.parseInt(new String(buffer).split("[^0-9]")[0]);
    }
}