package com.clemSP.iteration1.model;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;


public abstract class Dataset
{
    // Values of the nominal attributes
    protected static final String[] LOCATIONS = {"unknown", "UK", "International"};
    protected static final String[] POVS = {"unknown", "First", "Third"};
    protected final String[] DETECTIVES = {"unknown", "Hercule Poirot", "Tommy and Tuppence",
            "Colonel Race", "Superintendent Battle", "Miss Marple", "Mystery novel"};
    protected final String[] WEAPONS = {"unknown", "Poison", "Stabbing", "Accident", "Shooting",
            "Strangling", "Concussion", "ThroatSlit", "None", "Drowning"};
    protected final String[] VICTIMS = {"unknown", "F", "M", "None"};
    protected final String[] MURDERERS = {"unknown", "F", "M", "Lots", "None"};

    protected ArrayList<Attribute> mAttributes;

    /* Indices of the attributes of the instances. */
    protected static final int YEAR = AppAttribute.Year.getIndex(),
            DETECTIVE = AppAttribute.Detective.getIndex(),
            LOCATION = AppAttribute.Location.getIndex(), POV = AppAttribute.Pov.getIndex(),
            WEAPON = AppAttribute.Weapon.getIndex(), VICTIM = AppAttribute.Victim.getIndex(),
            MURDERER = AppAttribute.Murderer.getIndex(), AVG_RATINGS = 7, NUM_RATINGS = 8;

    protected Instances mData, mLabelled;
    protected String mTitle, mLabel;

    protected AppClassifier mClassifier;

    protected int mClassIndex;


    public Dataset(Context context, String model, int classIndex)
    {
        setAttributeList();

        mClassifier = AppClassifier.get(context, model);

        mClassIndex = classIndex;
    }


    private void setAttributeList()
    {
        mAttributes = new ArrayList<>(9);

        // Setting the numeric attributes
        mAttributes.add(new Attribute("year"));

        List<String> detectiveValues = new ArrayList<>(DETECTIVES.length - 1);
        Collections.addAll(detectiveValues, DETECTIVES);
        mAttributes.add(new Attribute("detective", detectiveValues));

        List<String> locationValues = new ArrayList<>(LOCATIONS.length - 1);
        Collections.addAll(locationValues, LOCATIONS);
        mAttributes.add(new Attribute("location", locationValues));

        List<String> povValues = new ArrayList<>(POVS.length - 1);
        Collections.addAll(povValues, POVS);
        mAttributes.add(new Attribute("point_of_view", povValues));

        List<String> weaponValues = new ArrayList<>(WEAPONS.length - 1);
        Collections.addAll(weaponValues, WEAPONS);
        mAttributes.add(new Attribute("murder_weapon", weaponValues));

        List<String> victimValues = new ArrayList<>(VICTIMS.length - 1);
        Collections.addAll(victimValues, VICTIMS);
        mAttributes.add(new Attribute("victim_gender", victimValues));

        List<String> murdererValues = new ArrayList<>(MURDERERS.length - 1);
        Collections.addAll(murdererValues, MURDERERS);
        mAttributes.add(new Attribute("murderer_gender", murdererValues));

        mAttributes.add(new Attribute("average_ratings"));
        mAttributes.add(new Attribute("number_of_ratings"));
    }


    public ArrayList<Attribute> getAttributes(int... indices)
    {
        ArrayList<Attribute> attributes = new ArrayList<>(indices.length);

        for(int index : indices)
            attributes.add(mAttributes.get(index));

        return attributes;
    }


    public void setData(double[] values)
    {
        mData.add(new DenseInstance(1.0, values));
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
