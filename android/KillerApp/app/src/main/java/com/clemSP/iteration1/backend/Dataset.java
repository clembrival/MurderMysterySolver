package com.clemSP.iteration1.backend;

import android.app.Activity;

import com.clemSP.iteration1.frontend.PredictionSettings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import com.clemSP.iteration1.backend.AttributeFactory.NominalValues;
import com.clemSP.iteration1.backend.AttributeFactory.AppAttribute;


public class Dataset
{
    private static Dataset sDataset;

    private boolean mPredictWeapon;
    private Instances mData, mLabelled;
    private String mTitle, mLabel;

    private AppClassifier mClassifier;

    private int mClassIndex;


    public static Dataset get(Activity activity)
    {
        if(sDataset == null)
        {
            boolean predictWeapon = PredictionSettings.getSettings().getPredictWeapon();

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


    public static ArrayList<Attribute> getAttributeList()
    {
        ArrayList<Attribute> attributes = new ArrayList<>(AppAttribute.getNumAttributes());

        // Setting the numeric attributes
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

        attributes.add(new Attribute("average_ratings"));

        return attributes;
    }


    public static void clear()
    {
    	sDataset = null;
    }


    public void setData(Data data)
    {
        mTitle = data.getTitle();

        // 0 = capacity of the dataset
        mData = new Instances("data", getAttributeList(), 0);

        double[] values = new double[mData.numAttributes()];

        int index = 0;

        values[index++] = Integer.parseInt(data.getYear());
        values[index] = mData.attribute(index++).indexOfValue(data.getDetective());
        values[index] = mData.attribute(index++).indexOfValue(data.getLocation());
        values[index] = mData.attribute(index++).indexOfValue(data.getPov());
        values[index] = mData.attribute(index++).indexOfValue(data.getWeapon());
        values[index] = mData.attribute(index++).indexOfValue(data.getVictim());
        values[index] = mData.attribute(index++).indexOfValue(data.getGender());
        values[index] = Double.parseDouble(data.getRating());

        Instance instance = new DenseInstance(1.0, values);

        for(index = 0; index < values.length; index++)
        {
            String value = data.getValue(index+1);
            if(value.equals("unknown") ||
                    (mData.attribute(index).isNumeric() && instance.value(index) == 0))
                instance.setMissing(index);
        }

        // each attribute gets a weight of 1
        mData.add(instance);
    }


    public String classify()
    {
        mLabelled = mClassifier.classify(mPredictWeapon, mData, mClassIndex);

        mLabel = mLabelled.instance(mData.numInstances()-1).stringValue(mClassIndex);

        return mLabel;
    }


    public int getConfidence()
    {
        double[] prediction = mClassifier.getDistribution(mPredictWeapon, mLabelled, 0);

        int classValueIndex = mLabelled.attribute(mClassIndex).indexOfValue(mLabel);

        return (int)(prediction[classValueIndex] * 100);
    }


    public void retrainClassifier(Activity activity, String rightAnswer)
    {
    	mLabelled.instance(0).setClassValue(mData.attribute(mClassIndex).indexOfValue(rightAnswer));
    	
        mClassifier.retrain(mPredictWeapon, activity, mLabelled);
    }    
    
    
    public Data getLabelledData()
    {
    	Data data = new Data();
    	
    	data.setTitle(mTitle);

    	Instance labelled = mLabelled.instance(mData.numInstances()-1);

    	for(int index = 0; index < labelled.numAttributes(); index++)
    	{
    		String value;
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

    		data.setAttribute(AppAttribute.getAttributeFromIndex(index), value);
    	}
    	
    	return data;
    }
}
