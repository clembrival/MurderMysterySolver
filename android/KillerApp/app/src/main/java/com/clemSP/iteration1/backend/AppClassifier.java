package com.clemSP.iteration1.backend;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;

import weka.classifiers.Classifier;
import weka.classifiers.UpdateableClassifier;
import weka.core.Instance;
import weka.core.Instances;


public class AppClassifier
{
    private static AppClassifier sClassifier;
    private Classifier mClassifier;

    private static String mModel;

    public static AppClassifier get(Context context, String modelFile)
    {
        if(sClassifier == null || !modelFile.equals(mModel))
            sClassifier = new AppClassifier(context, modelFile);
        return sClassifier;
    }


    private AppClassifier(Context context, String modelFile)
    {
        try
        {
            mModel = modelFile;

            // Loading the model
            ObjectInputStream ois = new ObjectInputStream(context.getAssets().open(mModel));

            // Creating the classifier from the model
            mClassifier = (weka.classifiers.Classifier) ois.readObject();
            ois.close();
        }
        catch(IOException ioe)
        {
            Log.e("AppClassifier", "IOException caught", ioe);
        }
        catch(Exception e)
        {
            Log.e("AppClassifier", "Exception caught", e);
        }
    }


    public Instances classify(Instances data, int classIndex)
    {
        try
        {
            // Loading the data to be classified
            data.setClassIndex(classIndex);

            Instances labelled = new Instances(data);

            // Classifying each instance
            for(int i = 0; i < data.numInstances(); i++)
            {
                double label = mClassifier.classifyInstance(data.instance(i));
                labelled.instance(i).setClassValue(label);
            }

            return labelled;
        }
        catch(IOException ioe)
        {
            Log.e("AppClassifier", "IOException caught", ioe);
        }
        catch(Exception e)
        {
            Log.e("AppClassifier", "Exception caught", e);
        }
        return null;
    }


    /* Retrains a classifier with a single instance. */
    public void retrain(Instance newInstance)
    {
        try
        {
            UpdateableClassifier newClassifier = (UpdateableClassifier) mClassifier;
            newClassifier.updateClassifier(newInstance);
        }
        catch(Exception e)
        {
            Log.e("AppClassifier", "Exception caught", e);
        }
    }


    public double[] getDistribution(Instances data, int instanceIndex)
    {
        try
        {
            return mClassifier.distributionForInstance(data.get(instanceIndex));
        }
        catch(Exception e)
        {
            Log.e("AppClassifier", "Exception caught", e);
            return null;
        }
    }
}
