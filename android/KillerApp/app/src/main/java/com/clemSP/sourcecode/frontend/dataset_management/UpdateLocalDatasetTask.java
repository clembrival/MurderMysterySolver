package com.clemSP.sourcecode.frontend.dataset_management;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.clemSP.sourcecode.R;
import com.clemSP.sourcecode.backend.Data;

import java.io.IOException;
import java.io.PrintWriter;


public class UpdateLocalDatasetTask extends DatasetTask
{
    private Data mData;


    public UpdateLocalDatasetTask(Activity activity, String url, Data data)
    {
        super(R.string.update_local, activity, url);

        mData = data;
    }


    @Override
    protected Integer doInBackground(Void... params)
    {
        boolean status = updateLocalDataset();

        if(status)
            Log.w("UpdateLocalDataset", "Updated the local dataset.");

        return status ? POSITIVE_RESULT : ERROR;
    }


    private boolean updateLocalDataset()
    {
        PrintWriter writer = null;

        try
        {
            try
            {
                writer = new PrintWriter(mActivity.getBaseContext().openFileOutput("dataset.arff",
                        Context.MODE_APPEND));

                writer.println(mData.toString());

                return true;
            }
            finally
            {
                if(writer != null)
                    writer.close();
            }
        }
        catch (IOException ioe)
        {
            Log.w("UpdateLocalDatasetTask", ioe.getLocalizedMessage());
            return false;
        }
    }
}
