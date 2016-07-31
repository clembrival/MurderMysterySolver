package com.clemSP.iteration1.backend;

import android.content.Context;

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

    private ArrayList<Attribute> mAttributes;

    private Instances mData, mLabelled;
    private String mTitle, mLabel;

    private AppClassifier mClassifier;

    private int mClassIndex;


    public static Dataset get(Context context)
    {
        if(sDataset == null)
        {
            boolean predictWeapon = PredictionSettings.getSettings().getPredictWeapon();
            String model = predictWeapon ? "ibk_weapon.model" : "ibk_gender.model";
            int classIndex = predictWeapon ? AppAttribute.Weapon.getIndex()
                    : AppAttribute.Murderer.getIndex();

            sDataset = new Dataset(context, model, classIndex);
        }
        return sDataset;
    }


    private Dataset(Context context, String model, int classIndex)
    {
        setAttributeList();

        mClassifier = AppClassifier.get(context, model);

        mClassIndex = classIndex;
    }


    private void setAttributeList()
    {
        mAttributes = new ArrayList<>(AppAttribute.getNumAttributes());

        // Setting the numeric attributes
        mAttributes.add(new Attribute(AppAttribute.Year.getLabel()));

        List<String> detectiveValues = new ArrayList<>(NominalValues.DETECTIVES.length - 1);
        Collections.addAll(detectiveValues, NominalValues.DETECTIVES);
        mAttributes.add(new Attribute(AppAttribute.Detective.getLabel(), detectiveValues));

        List<String> locationValues = new ArrayList<>(NominalValues.LOCATIONS.length - 1);
        Collections.addAll(locationValues, NominalValues.LOCATIONS);
        mAttributes.add(new Attribute(AppAttribute.Location.getLabel(), locationValues));

        List<String> povValues = new ArrayList<>(NominalValues.POVS.length - 1);
        Collections.addAll(povValues, NominalValues.POVS);
        mAttributes.add(new Attribute(AppAttribute.Pov.getLabel(), povValues));

        List<String> weaponValues = new ArrayList<>(NominalValues.WEAPONS.length - 1);
        Collections.addAll(weaponValues, NominalValues.WEAPONS);
        mAttributes.add(new Attribute(AppAttribute.Weapon.getLabel(), weaponValues));

        List<String> victimValues = new ArrayList<>(NominalValues.VICTIMS.length - 1);
        Collections.addAll(victimValues, NominalValues.VICTIMS);
        mAttributes.add(new Attribute(AppAttribute.Victim.getLabel(), victimValues));

        List<String> murdererValues = new ArrayList<>(NominalValues.MURDERERS.length - 1);
        Collections.addAll(murdererValues, NominalValues.MURDERERS);
        mAttributes.add(new Attribute(AppAttribute.Murderer.getLabel(), murdererValues));

        mAttributes.add(new Attribute("average_ratings"));
        //mAttributes.add(new Attribute("number_of_ratings"));
    }


    public static void clear()
    {
        sDataset = null;
    }


    public void setData(Data data)
    {
        mTitle = data.getTitle();

        // 0 = capacity of the dataset
        mData = new Instances("data", mAttributes, 0);

        double[] values = new double[mData.numAttributes()];

        int index = 0;

        values[index++] = Integer.parseInt(data.getYear());
        values[index] = mData.attribute(index++).indexOfValue(data.getDetective());
        values[index] = mData.attribute(index++).indexOfValue(data.getSetting());
        values[index] = mData.attribute(index++).indexOfValue(data.getPov());
        values[index] = mData.attribute(index++).indexOfValue(data.getWeapon());
        values[index] = mData.attribute(index++).indexOfValue(data.getVictim());
        values[index] = mData.attribute(index++).indexOfValue(data.getGender());
        values[index] = Double.parseDouble(data.getYear());

        Instance instance = new DenseInstance(1.0, values);

        for(index = 0; index < values.length; index++)
        {
            String value = data.getValue(index);
            if(value.equals("unknown")
                    || (mData.attribute(index).isNumeric() && instance.value(index) == 0))
                instance.setMissing(index);
        }

        // each attribute gets a weight of 1
        mData.add(instance);
    }


    public String classify()
    {
        mLabelled = mClassifier.classify(mData, mClassIndex);

        mLabel = mLabelled.instance(mData.numInstances()-1).stringValue(mClassIndex);

        return mLabel;
    }


    public int getConfidence()
    {
        double[] prediction = mClassifier.getDistribution(mLabelled, 0);

        int classValueIndex = mLabelled.attribute(mClassIndex).indexOfValue(mLabel);

        return (int)(prediction[classValueIndex] * 100);
    }


    public void retrainClassifier()
    {
        mClassifier.retrain(mLabelled.instance(0));
    }
}
