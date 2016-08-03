package com.clemSP.iteration1.frontend.dataset_management;

import android.app.Activity;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;

import weka.classifiers.Classifier;


public class StreamManager
{
    public static boolean printStreamToInternalStorage(Activity activity, InputStream stream,
                                                       String filename, String tag)
    {
        OutputStreamWriter writer = null;

        try
        {
            try
            {
                writer = new OutputStreamWriter(activity.getBaseContext()
                        .openFileOutput(filename, Activity.MODE_PRIVATE));

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
            Log.e(tag, Arrays.toString(ioe.getStackTrace()));
        }
        return false;
    }


    public static boolean objectToInternalStorage(Activity activity, InputStream stream,
                                                  String filename, String tag)
    {
        ObjectInputStream inputStream = null;
        ObjectOutputStream outputStream = null;

        try
        {
            try
            {
                inputStream = new ObjectInputStream(stream);
                Classifier classifier = (Classifier) inputStream.readObject();

                outputStream = new ObjectOutputStream(activity.getBaseContext()
                        .openFileOutput(filename, Activity.MODE_PRIVATE));

                outputStream.writeObject(classifier);
                outputStream.flush();

                return true;
            }
            finally
            {
                if(inputStream != null)
                    inputStream.close();
                if(outputStream != null)
                    outputStream.close();
            }
        }
        catch(Exception e)
        {
            Log.e(tag, Arrays.toString(e.getStackTrace()));
        }
        return false;
    }
}
