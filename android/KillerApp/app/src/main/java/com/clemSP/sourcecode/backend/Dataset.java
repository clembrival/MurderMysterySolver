package com.clemSP.sourcecode.backend;

import android.app.Activity;

import com.clemSP.sourcecode.frontend.PredictionSettings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import com.clemSP.sourcecode.backend.AttributeFactory.NominalValues;
import com.clemSP.sourcecode.backend.AttributeFactory.AppAttribute;


/** Class handling the data to be classified. */
public class Dataset
{
    private static Dataset sDataset;

    /** Whether the attribute to be predicted is the cause of death (true)
      * or the murderer's gender (false). */
    private boolean mPredictWeapon;

    /** The unclassified data. */
    private Instances mData;
    /** The classified data. */
    private Instances mLabelled;

    /** The title of the book to be classified. */
    private String mTitle;

    /** The value of the class assigned to the data.*/
    private String mLabel;

    /** A reference to the classifier. */
    private AppClassifier mClassifier;

    /** The index of the attribute to be predicted (cause of death or murderer's gender). */
    private int mClassIndex;

    /** The number of attributes with a missing value. */
    private int mMissingCount;


    /**
     * @return a reference to an existing Dataset instance, if any,
     *         or a new Dataset instance, if none.
     * @param activity the activity requesting the Dataset instance.
     */
    public static Dataset get(Activity activity)
    {
        if(sDataset == null)
        {
            // Get the attribute to be predicted from the settings of the prediction
            boolean predictWeapon = PredictionSettings.getSettings().getPredictWeapon();

            // Get the index corresponding to the attribute to be predicted
            int classIndex = predictWeapon ? AppAttribute.Weapon.getIndex()
                    : AppAttribute.Murderer.getIndex();

            sDataset = new Dataset(activity, classIndex, predictWeapon);
        }
        return sDataset;
    }


    private Dataset(Activity activity, int classIndex, boolean predictWeapon)
    {
        mClassifier = AppClassifier.get(activity);

        mClassIndex = classIndex;

        mPredictWeapon = predictWeapon;
    }


    /**
     * @return a list of attributes to be used to initialise an Instances object.
     */
    public static ArrayList<Attribute> getAttributeList()
    {
        ArrayList<Attribute> attributes = new ArrayList<>(AppAttribute.getNumAttributes());

        attributes.add(new Attribute(AppAttribute.Year.getLabel()));

        List<String> detectiveValues = new ArrayList<>(NominalValues.DETECTIVES.length - 1);
        Collections.addAll(detectiveValues, NominalValues.DETECTIVES);
        attributes.add(new Attribute(AppAttribute.Detective.getLabel(), detectiveValues));

        List<String> locationValues = new ArrayList<>(NominalValues.LOCATIONS.length - 1);
        Collections.addAll(locationValues, NominalValues.LOCATIONS);
        attributes.add(new Attribute(AppAttribute.Location.getLabel(), locationValues));

        List<String> povValues = new ArrayList<>(NominalValues.POVS.length - 1);
        Collections.addAll(povValues, NominalValues.POVS);
        attributes.add(new Attribute(AppAttribute.Pov.getLabel(), povValues));

        List<String> weaponValues = new ArrayList<>(NominalValues.WEAPONS.length - 1);
        Collections.addAll(weaponValues, NominalValues.WEAPONS);
        attributes.add(new Attribute(AppAttribute.Weapon.getLabel(), weaponValues));

        List<String> victimValues = new ArrayList<>(NominalValues.VICTIMS.length - 1);
        Collections.addAll(victimValues, NominalValues.VICTIMS);
        attributes.add(new Attribute(AppAttribute.Victim.getLabel(), victimValues));

        List<String> murdererValues = new ArrayList<>(NominalValues.MURDERERS.length - 1);
        Collections.addAll(murdererValues, NominalValues.MURDERERS);
        attributes.add(new Attribute(AppAttribute.Murderer.getLabel(), murdererValues));

        attributes.add(new Attribute(AppAttribute.Rating.getLabel()));

        return attributes;
    }


    /**
     * Resets the existing Dataset.
     */
    public static void clear()
    {
    	sDataset = null;
    }


    /**
     * Sets the values of the attributes of the Instances to be classified.
     * @param data the data entered by the user.
     */
    public void setData(Data data)
    {
        mTitle = data.getTitle();

        // 0 = capacity of the dataset
        mData = new Instances("data", getAttributeList(), 0);

        // All the values of the attributes are stored as doubles
        double[] values = new double[mData.numAttributes()];

        int index = 0;

        // Match the strings stored in data to the corresponding numeric or nominal value
        values[index++] = Integer.parseInt(data.getYear());
        values[index] = mData.attribute(index++).indexOfValue(data.getDetective());
        values[index] = mData.attribute(index++).indexOfValue(data.getLocation());
        values[index] = mData.attribute(index++).indexOfValue(data.getPov());
        values[index] = mData.attribute(index++).indexOfValue(data.getWeapon());
        values[index] = mData.attribute(index++).indexOfValue(data.getVictim());
        values[index] = mData.attribute(index++).indexOfValue(data.getGender());
        values[index] = Double.parseDouble(data.getRating());

        // Create a new instance with the values array
        // each attribute gets a weight of 1.0
        Instance instance = new DenseInstance(1.0, values);

        mMissingCount = 0;
        // Set any unknown value as missing
        for(index = 0; index < values.length; index++)
        {
            String value = data.getValue(index+1);
            if(value.equals("unknown") ||
                    (mData.attribute(index).isNumeric() && instance.value(index) < 0.5))
            {
                instance.setMissing(index);
                mMissingCount++;
            }
        }

        mData.add(instance);
    }


    /**
     * Classifies the Instances for the attribute saved in the prediction's settings.
     * @return the value assigned to the attribute to be predicted after classification.
     */
    public String classify()
    {
        mLabelled = mClassifier.classify(mPredictWeapon, mData, mClassIndex);

        // Get the predicted value
        mLabel = mLabelled.instance(mData.numInstances()-1).stringValue(mClassIndex);

        return mLabel;
    }


    /**
     * @return the distribution of the attribute to be predicted as a percentage (between 0 and 100).
     */
    public int getConfidence()
    {
        // Get the distribution for all the value of the attribute to be predicted
        double[] prediction = mClassifier.getDistribution(mPredictWeapon, mLabelled, 0);

        // Get the index of the actual predicted value
        int classValueIndex = mLabelled.attribute(mClassIndex).indexOfValue(mLabel);

        return (int)(prediction[classValueIndex] * 100);
    }


    /**
     * @return the number of attributes with missing values.
     */
    public int getMissingCount()
    {
        return mMissingCount;
    }


    /**
     * Updates the classifier with the right answer
     * given after the user invalidated an incorrect prediction.
     * @param activity the activity requesting the update.
     * @param rightAnswer the value that should have been predicted for the input data.
     */
    public void retrainClassifier(Activity activity, String rightAnswer)
    {
    	mLabelled.instance(0).setClassValue(mData.attribute(mClassIndex).indexOfValue(rightAnswer));
    	
        mClassifier.retrain(mPredictWeapon, activity, mLabelled);
    }


    /**
     * @return the labelled data with double values converted to strings.
     */
    public Data getLabelledData()
    {
    	Data data = new Data();
    	
    	data.setTitle(mTitle);

    	Instance labelled = mLabelled.instance(mData.numInstances()-1);

    	for(int index = 0; index < labelled.numAttributes(); index++)
    	{
    		String value;
            if(labelled.isMissing(index))
                value = "?";
            else
            {
                if(labelled.attribute(index).isNumeric())
                {
                    double numericValue = labelled.value(index);
                    if(index == AppAttribute.Year.getIndex())
                        value = "" + ((int)numericValue);
                    else
                        value = "" + numericValue;
                }
                else
                    value = labelled.stringValue(index);
            }

    		data.setAttribute(AppAttribute.getAttributeFromIndex(index), value);
    	}
    	
    	return data;
    }
}