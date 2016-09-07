package com.clemSP.sourcecode.backend;

import android.app.Activity;
import android.util.Log;

import java.io.ObjectInputStream;

import com.clemSP.sourcecode.frontend.dataset_management.StreamManager;

import weka.classifiers.Classifier;
import weka.classifiers.UpdateableClassifier;
import weka.core.Instance;
import weka.core.Instances;


/** Class manipulating the classifier objects, responsible for the predictions. */
public class AppClassifier
{
    /** Tag for log output. */
    private static final String TAG = "AppClassifier";

    /** Names of the files containing the two classifiers. */
    private static final String WEAPON_MODEL = "ibk_weapon.model";
    private static final String GENDER_MODEL = "ibk_gender.model";

    private static AppClassifier sClassifier;

    private Classifier mWeaponClassifier, mGenderClassifier;


    /**
     * @return a reference to an existing AppClassifier instance, if any,
     *         or a new AppClassifier instance, if none.
     * @param activity the activity requesting the AppClassifier instance.
     */
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
                stream = new ObjectInputStream(activity.getBaseContext()
                        .openFileInput(WEAPON_MODEL));
                
                // Creating the classifier from the model
                mWeaponClassifier = (Classifier) stream.readObject();

                stream.close();

                // Loading the model
                stream = new ObjectInputStream(activity.getBaseContext()
                        .openFileInput(GENDER_MODEL));

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
            Log.e(TAG, e.getMessage());
        }
    }


    /**
     * Makes a prediction on all the instances of a dataset.
     * @param predictWeapon true to predict the cause of death,
     *                      false to predict the murderer's gender.
     * @param data the instances to be classified.
     * @param classIndex the index of the prediction's target (cause of death or murderer's gender)
     * @return the classified instances.
     */
    public Instances classify(boolean predictWeapon, Instances data, int classIndex)
    {
        try
        {
            Instances new_data = new Instances(data);
            new_data.setClassIndex(classIndex);

            // Loading the required classifier
            Classifier classifier = predictWeapon ? mWeaponClassifier : mGenderClassifier;

            // Classifying each instance
            for(int i = 0; i < new_data.numInstances(); i++)
            {
                double label = classifier.classifyInstance(new_data.instance(i));
                new_data.instance(i).setClassValue(label);
            }

            return new_data;
        }
        catch(Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }


    /**
     * Retrains a classifier with the given instances.
     * @param predictWeapon true to predict the cause of death,
     *                      false to predict the murderer's gender.
     * @param activity the activity requesting the update of the classifier.
     * @param newInstances the instances the classifier has to train on.
     * @return true if the update was completed, false otherwise.
     */
    public boolean retrain(boolean predictWeapon, Activity activity, Instances newInstances)
    {
        try
        {
            // Load the classifier to be updated
            UpdateableClassifier newClassifier = (UpdateableClassifier)
                    (predictWeapon ? mWeaponClassifier : mGenderClassifier);

            // Train the classifier on each one of the new instances
            for(Instance instance : newInstances)
                newClassifier.updateClassifier(instance);

            // Output the updated classifier to the corresponding .model file
            String modelFile = predictWeapon ? WEAPON_MODEL : GENDER_MODEL;
            StreamManager.classifierToInternalStorage(activity, (Classifier) newClassifier, 
            		modelFile, TAG);

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


    /**
     * Computes the estimated probability for each value of the attribute to be predicted.
     * @param predictWeapon true to predict the cause of death,
     *                      false to predict the murderer's gender.
     * @param data the classified dataset.
     * @param instanceIndex the index of the instance of interest.
     * @return an array containing the estimated probabilities of the instance
     *         for each attribute value, as a number between 0 and 1.
     */
    public double[] getDistribution(boolean predictWeapon, Instances data, int instanceIndex)
    {
        try
        {
            // Load the required classifier
            Classifier classifier = predictWeapon ? mWeaponClassifier : mGenderClassifier;

            return classifier.distributionForInstance(data.get(instanceIndex));
        }
        catch(Exception e)
        {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }
}