package com.clemSP.sourcecode.frontend.dataset_management;

import android.app.Activity;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;

import weka.classifiers.Classifier;


public class StreamManager
{
    public static boolean classifierToInternalStorage(Activity activity, Classifier classifier,
    												  String filename, String tag)
    {
        ObjectOutputStream outputStream = null;

        try
        {
            try
            {
                outputStream = new ObjectOutputStream(activity.getBaseContext()
                        .openFileOutput(filename, Activity.MODE_PRIVATE));

                outputStream.writeObject(classifier);
                outputStream.flush();

                return true;
            }
            finally
            {
                if(outputStream != null)
                    outputStream.close();
            }
        }
        catch(Exception e)
        {
            Log.e(tag, e.getLocalizedMessage());
        }
        return false;
    }
}
