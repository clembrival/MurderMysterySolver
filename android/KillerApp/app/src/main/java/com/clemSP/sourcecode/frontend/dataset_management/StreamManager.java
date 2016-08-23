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
    public static boolean printStreamToInternalStorage(Activity activity, InputStream stream,
                                                       String filename, String tag, int mode)
    {
        OutputStreamWriter writer = null;

        try
        {
            try
            {
                writer = new OutputStreamWriter(activity.getBaseContext()
                        .openFileOutput(filename, mode));

                int nextChar;
                while((nextChar = stream.read()) != -1)
                    writer.write((char) nextChar);

                return true;
            }
            finally
            {
                if(writer != null)
                    writer.close();
                if(stream != null)
                    stream.close();
            }
        }
        catch(IOException ioe)
        {
            Log.e(tag, ioe.getLocalizedMessage());
        }
        return false;
    }


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
