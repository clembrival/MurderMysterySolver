package com.clemSP.iteration1.model;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;

import weka.classifiers.Classifier;
import weka.classifiers.UpdateableClassifier;
import weka.core.Instance;
import weka.core.Instances;


public class KillerClassifier
{
    private static KillerClassifier sClassifier;
    private Classifier mClassifier;


    public static KillerClassifier get(Context context)
    {
        if(sClassifier == null)
            sClassifier = new KillerClassifier(context);
        return sClassifier;
    }


    private KillerClassifier(Context context)
    {
        try
        {
            // Loading the model (OLD MODEL)
            ObjectInputStream ois = new ObjectInputStream(context.getAssets().open("j48.model"));

            // Creating the classifier from the model
            mClassifier = (weka.classifiers.Classifier) ois.readObject();
            ois.close();
        }
        catch(IOException ioe)
        {
            Log.e("KillerClassifier", "IOException caught", ioe);
        }
        catch(Exception e)
        {
            Log.e("KillerClassifier", "Exception caught", e);
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
            Log.e("KillerClassifier", "IOException caught", ioe);
        }
        catch(Exception e)
        {
            Log.e("KillerClassifier", "Exception caught", e);
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
            Log.e("KillerClassifier", "Exception caught", e);
        }

    }
}
