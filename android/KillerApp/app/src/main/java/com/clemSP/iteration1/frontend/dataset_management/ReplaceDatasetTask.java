package com.clemSP.iteration1.frontend.dataset_management;

import android.app.Activity;
import android.util.Log;

import com.clemSP.iteration1.R;
import com.clemSP.iteration1.backend.AppClassifier;
import com.clemSP.iteration1.backend.AttributeFactory.AppAttribute;
import com.clemSP.iteration1.backend.Dataset;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;


/** Class implementing a background task to update the content of the local dataset
  * with the server's new entries and retrain the classifiers with the new data. */
public class ReplaceDatasetTask extends DatasetTask
{
    private static final String TAG = "ReplaceDatasetTask";


    public ReplaceDatasetTask(Activity activity, String url)
    {
        super(R.string.update_local, activity, url);
    }


    @Override
    protected Integer doInBackground(Void... params)
    {
        // Download the entries newly added to the server
        String newEntries = getNewEntries();

        if(newEntries == null)
            return ERROR;

        if("".equals(newEntries))
            return NEGATIVE_RESULT;

        Log.w(TAG, "Downloaded the new entries from the server.");

        // Create Instances from downloaded string
        Instances serverDataset = getInstances(newEntries);
        if(serverDataset != null)
        {
            Log.w(TAG, "Created instances from server dataset.");
            if(retrainClassifiers(serverDataset))
            {
                Log.w(TAG, "Retrained both classifiers and output model files.");
                return POSITIVE_RESULT;
            }
        }

        return ERROR;
    }


    /**
     * Connects to the killerapp/new_entries/ url
     * @return a String containing the entries added to the server since the last update
     */
    private String getNewEntries()
    {
        HttpsURLConnection connection = null;
        InputStream inputStream = null;
        StringWriter writer = null;
        String timestamp = mSharedPref.getString("timestamp", "");

        try
        {
            try
            {
                String query = String.format("timestamp=%s", URLEncoder.encode(timestamp, "UTF-8"));

                connection = getConnection(false, "?" + query, 0);
                connection.connect();

                inputStream = connection.getInputStream();

                // Retrieve the entries returned by the server
                writer = new StringWriter();

                int nextChar;
                while((nextChar = inputStream.read()) != -1)
                    writer.write((char) nextChar);

                return writer.getBuffer().toString();
            }
            finally
            {
                if(writer != null) writer.close();
                if(inputStream != null) inputStream.close();
                if(connection != null) connection.disconnect();
            }
        }
        catch(Exception e)
        {
            Log.e(TAG, e.getLocalizedMessage());
        }
        return null;
    }


    /**
     * @param entries: String containing the new entries
     * @return an Instances object with the new entries
     */
    private Instances getInstances(String entries)
    {
        File outputFile = null;
        FileInputStream tempInputStream = null;

        try
        {
            try
            {
                // Copy new entries in a temporary file
                outputFile = createTempFile(entries);
                if(outputFile == null)
                    return null;

                tempInputStream = new FileInputStream(outputFile);

                // Create Instances from the temp file
                Instances newEntries = new Instances("new_entries", Dataset.getAttributeList(), 0);
                for(Instance instance : ConverterUtils.DataSource.read(tempInputStream))
                    newEntries.add(instance);

                // Copy the content of the file to
                StreamManager.printStreamToInternalStorage(mActivity, tempInputStream, DATASET_FILE,
                        TAG, Activity.MODE_APPEND);

                return newEntries;
            }
            finally
            {
                if(tempInputStream != null) tempInputStream.close();
                if(outputFile != null) outputFile.delete();
            }
        }
        catch (Exception ioe)
        {
            Log.e(TAG, ioe.getMessage());
        }
        return null;
    }


    private File createTempFile(String entries)
    {
        final String TEMP_FILENAME = "tempDataset.arff";
        final String HEADER = "@relation novel\n\n" +
                "@attribute title string\n" +
                "@attribute year numeric\n" +
                "@attribute detective {unknown, \"Hercule Poirot\", " +
                "\"Tommy and Tuppence\", \"Colonel Race\", \"Superintendent Battle\", " +
                "\"Miss Marple\", \"Mystery novel\"}\n" +
                "@attribute location {unknown, UK, International}\n" +
                "@attribute point_of_view {unknown, First, Third}\n" +
                "@attribute murder_weapon {unknown, Poison, Stabbing, Accident, " +
                "Shooting, Strangling, Concussion, ThroatSlit, None, Drowning}\n" +
                "@attribute victim_gender {unknown, F, M, None}\n" +
                "@attribute murderer_gender {unknown, F, M, Lots, None}\n" +
                "@attribute average_ratings numeric\n\n" +
                "@data";

        File outputFile;
        PrintWriter writer = null;

        try
        {
            try
            {
                outputFile = File.createTempFile(TEMP_FILENAME, null, mActivity.getCacheDir());

                writer = new PrintWriter(outputFile);
                writer.println(HEADER);
                writer.println(entries);
                writer.close();

                return outputFile;
            }
            finally
            {
                if(writer != null) writer.close();
            }
        }
        catch (Exception ioe)
        {
            Log.e(TAG, ioe.getMessage());
        }
        return null;
    }


    private boolean retrainClassifiers(Instances serverDataset)
    {
        serverDataset.setClassIndex(AppAttribute.Weapon.getIndex());

        if(AppClassifier.get(mActivity).retrain(true, mActivity, serverDataset))
        {
            serverDataset.setClassIndex(AppAttribute.Murderer.getIndex());
            return AppClassifier.get(mActivity).retrain(false, mActivity, serverDataset);
        }

        return false;
    }



}
