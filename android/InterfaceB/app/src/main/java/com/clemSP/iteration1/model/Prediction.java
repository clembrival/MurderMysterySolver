package com.clemSP.iteration1.model;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;


public class Prediction
{
    private final String[] DETECTIVES = {"Hercule Poirot", "Tommy and Tuppence", "Colonel Race",
            "Superintendent Battle", "Miss Marple", "Mystery novel"};
    private final String[] WEAPONS = {"Poison", "Stabbing", "Accident", "Shooting", "Strangling",
            "Concussion", "ThroatSlit", "None", "Drowning"};

    private String mTitle;
    private Instances mData;


    private ArrayList<Attribute> getAttributes()
    {
        Attribute titleAtt = new Attribute("title");
        Attribute yearAtt = new Attribute("year");

        List<String> locationValues = new ArrayList<>(2);
        locationValues.add("UK");
        locationValues.add("International");
        Attribute locationAtt = new Attribute("location", locationValues);

        List<String> povValues = new ArrayList<>(2);
        povValues.add("First");
        povValues.add("Third");
        Attribute povAtt = new Attribute("point_of_view", povValues);

        List<String> detectiveValues = new ArrayList<>(6);
        Collections.addAll(detectiveValues, DETECTIVES);
        Attribute detectiveAtt = new Attribute("detective", detectiveValues);

        List<String> weaponValues = new ArrayList<>(9);
        Collections.addAll(weaponValues, WEAPONS);
        Attribute weaponAtt = new Attribute("murder_weapon", weaponValues);

        ArrayList<Attribute> attributes = new ArrayList<>(6);
        attributes.add(titleAtt);
        attributes.add(yearAtt);
        attributes.add(locationAtt);
        attributes.add(povAtt);
        attributes.add(detectiveAtt);
        attributes.add(weaponAtt);

        return attributes;
    }


    public void setData(String title, int year, String setting, String pov, String detective)
    {
        mTitle = title;

        mData = new Instances("data", getAttributes(), 0);

        double[] values = new double[mData.numAttributes()];
        values[0] = mData.attribute(0).addStringValue(title);
        values[1] = year;
        values[2] = mData.attribute(2).indexOfValue(setting);
        values[3] = mData.attribute(3).indexOfValue(pov);
        values[4] = mData.attribute(4).indexOfValue(detective);

        mData.add(new DenseInstance(1.0, values));
    }


    public Instances classify(Context context)
    {
        try
        {
            // Loading the model
            ObjectInputStream ois = new ObjectInputStream(context.getAssets().open("j48.model"));

            // Creating the classifier from the model
            Classifier classifier = (Classifier) ois.readObject();
            ois.close();

            // Loading the data to be classified
            mData.setClassIndex(mData.numAttributes() - 1);

            Instances labelled = new Instances(mData);

            // Classifying each instance
            for(int i = 0; i < mData.numInstances(); i++)
            {
                double label = classifier.classifyInstance(mData.instance(i));
                labelled.instance(i).setClassValue(label);
                labelled.attribute(0).addStringValue(mTitle);
            }

            return labelled;
        }
        catch(IOException ioe)
        {
            Log.e("Prediction", "IOException caught", ioe);
        }
        catch(Exception e)
        {
            Log.e("Prediction", "Exception caught", e);
        }
        return null;
    }
}
