package com.clemSP.sourcecode.frontend.dataset_management;

import android.app.Activity;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;

import weka.classifiers.Classifier;


/**
  * This class is a utility class containing methods to output streams to files or storage.
  */
public class StreamManager
{
    /**
      * Outputs a classifier to a file.
      * @param activity the activity requesting the output
      * @param classifier the classifier to be output
      * @param filename the name of the file to which the classifier is to be output
      * @param tag the log tag of the activity requesting the ouput
      */
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
