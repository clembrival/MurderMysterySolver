package com.clemSP.iteration1.backend;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import weka.classifiers.Classifier;
import weka.classifiers.UpdateableClassifier;
import weka.core.Instance;
import weka.core.Instances;


public class AppClassifier
{
    private static final String TAG = "AppClassifier";
    private static final String WEAPON_MODEL = "ibk_weapon.model";
    private static final String GENDER_MODEL = "ibk_gender.model";

    private static AppClassifier sClassifier;
    private Classifier mWeaponClassifier, mGenderClassifier;


    public static AppClassifier get(Activity activity)
    {
        if(sClassifier == null)
            sClassifier = new AppClassifier(activity);
        return sClassifier;
    }


    private AppClassifier(Activity activity)
    {
        ObjectInputStream stream = null;
        try
        {
            try
            {
                // Loading the model
                /*stream = new ObjectInputStream(activity.getBaseContext()
                        .openFileInput(WEAPON_MODEL));
                        */
                stream = new ObjectInputStream(activity.getAssets().open(WEAPON_MODEL));

                // Creating the classifier from the model
                mWeaponClassifier = (Classifier) stream.readObject();

                stream.close();

                // Loading the model
                /*stream = new ObjectInputStream(activity.getBaseContext()
                        .openFileInput(GENDER_MODEL));*/
                stream = new ObjectInputStream(activity.getAssets().open(WEAPON_MODEL));

                // Creating the classifier from the model
                mGenderClassifier = (Classifier) stream.readObject();
            }
            finally
            {
                if(stream != null)
                    stream.close();
            }
        }
        catch(Exception e)
        {
            Log.e(TAG, Arrays.toString(e.getStackTrace()));
        }
    }


    public Instances classify(boolean predictWeapon, Instances data, int classIndex)
    {
        try
        {
            // Loading the data to be classified
            data.setClassIndex(classIndex);

            Instances labelled = new Instances(data);
            Classifier classifier = predictWeapon ? mWeaponClassifier : mGenderClassifier;

            // Classifying each instance
            for(int i = 0; i < data.numInstances(); i++)
            {

                double label = classifier.classifyInstance(data.instance(i));
                labelled.instance(i).setClassValue(label);
            }

            return labelled;
        }
        catch(Exception e)
        {
            Log.e(TAG, Arrays.toString(e.getStackTrace()));
        }
        return null;
    }


    /* Retrains a classifier with the given instances. */
    public boolean retrain(boolean predictWeapon, Activity activity, Instances newInstances)
    {
        try
        {
            UpdateableClassifier newClassifier = (UpdateableClassifier)
                    (predictWeapon ? mWeaponClassifier : mGenderClassifier);

            for(Instance instance : newInstances)
                newClassifier.updateClassifier(instance);

            String modelFile = predictWeapon ? WEAPON_MODEL : GENDER_MODEL;
            outputModel(activity, (Classifier)newClassifier, modelFile);

            if(predictWeapon)
                mWeaponClassifier = (Classifier) newClassifier;
            else
                mGenderClassifier = (Classifier) newClassifier;

            return true;
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }


    private void outputModel(Activity activity, Classifier classifier, String modelFile) throws Exception
    {
        ObjectOutputStream stream = new ObjectOutputStream(activity.getBaseContext()
                .openFileOutput(modelFile, Context.MODE_APPEND));
        stream.writeObject(classifier);
        stream.flush();
        stream.close();
    }


    public double[] getDistribution(boolean predictWeapon, Instances data, int instanceIndex)
    {
        try
        {
            Classifier classifier = predictWeapon ? mWeaponClassifier : mGenderClassifier;
            return classifier.distributionForInstance(data.get(instanceIndex));
        }
        catch(Exception e)
        {
            Log.e(TAG, Arrays.toString(e.getStackTrace()));
            return null;
        }
    }
}