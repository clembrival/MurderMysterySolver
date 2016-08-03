package com.clemSP.iteration1.frontend.dataset_management;

import android.app.Activity;
import android.util.Log;

import com.clemSP.iteration1.R;
import com.clemSP.iteration1.backend.AppClassifier;
import com.clemSP.iteration1.backend.Dataset;

import java.net.URL;
import java.util.Arrays;

import javax.net.ssl.HttpsURLConnection;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;


public class ReplaceDatasetTask extends DatasetTask
{
    private static final String TAG = "ReplaceDatasetTask";


    public ReplaceDatasetTask(Activity activity, String url)
    {
        super(R.string.download_file, activity, url);
    }


    @Override
    protected Boolean doInBackground(Void... params)
    {
        Instances localDataset = getInstances();
        Log.w(TAG, "Created instances from local dataset.");

        Instances serverDataset;

        if(localDataset != null && updateDataset())
        {
            Log.w(TAG, "Updated local dataset from server file.");

            serverDataset = getInstances();
            Log.w(TAG, "Created instances from server dataset.");

            return serverDataset != null; //&& retrainClassifiers(localDataset, serverDataset);
/*
            if(status)
                Log.w(TAG, "Retrained both classifiers and output model files.");

            return status;
            */
        }

        return false;
    }


    private Instances getInstances()
    {
        try
        {
            return ConverterUtils.DataSource.read(mActivity.getBaseContext()
                    .openFileInput(DATASET_FILE));
        }
        catch (Exception e)
        {
            Log.e(TAG, Arrays.toString(e.getStackTrace()));
        }

        return null;
    }


    private boolean updateDataset()
    {
        HttpsURLConnection connection = null;

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

                return StreamManager.printStreamToInternalStorage(mActivity,
                        connection.getInputStream(), DATASET_FILE, TAG);
            }
            finally
            {
                if(connection != null)
                    connection.disconnect();
            }
        }
        catch(Exception e)
        {
            Log.e(TAG, Arrays.toString(e.getStackTrace()));
        }
        return false;
    }


    private boolean retrainClassifiers(Instances localDataset, Instances serverDataset)
    {
        Instances newEntries = new Instances("new_entries", Dataset.getAttributeList(), 0);

        for(Instance entry : serverDataset)
            if(!localDataset.contains(entry))
                newEntries.add(entry);

        return AppClassifier.get(mActivity).retrain(true, mActivity, newEntries) &&
                AppClassifier.get(mActivity).retrain(false, mActivity, newEntries);
    }
}
