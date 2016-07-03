package com.clemSP.iteration1.model;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;


public class Prediction
{
    private static Prediction sPrediction;

    private static final String[] LOCATIONS = {"unknown", "UK", "International"};
    private static final String[] POVS = {"unknown", "First", "Third"};
    private final String[] DETECTIVES = {"unknown", "Hercule Poirot", "Tommy and Tuppence", "Colonel Race",
            "Superintendent Battle", "Miss Marple", "Mystery novel"};
    private final String[] WEAPONS = {"Poison", "Stabbing", "Accident", "Shooting", "Strangling",
            "Concussion", "ThroatSlit", "None", "Drowning"};

    /* Indices of the attributes of the instances. */
    private static final int TITLE = 0, YEAR = 1, LOCATION = 2, POV = 3,
            DETECTIVE = 4, WEAPON = 5, AVG_RATINGS = 6, NUM_RATINGS = 7;

    private Instances mData, mLabelled;


    public static Prediction get(Context context)
    {
        if(sPrediction == null)
            sPrediction = new Prediction();
        return sPrediction;
    }

    private Prediction(){ }


    public Instance getLastInstance()
    {
        return mLabelled.instance(0);
    }


    private ArrayList<Attribute> getAttributes()
    {
        // Setting the string attribute
        Attribute titleAtt = new Attribute("title");

        // Setting the numeric attributes
        Attribute yearAtt = new Attribute("year");
       /* Attribute avgRatingsAtt = new Attribute("average_ratings");
        Attribute numRatingsAtt = new Attribute("number_of_ratings");*/

        // Setting the nominal attributes
        List<String> locationValues = new ArrayList<>(2);
        Collections.addAll(locationValues, LOCATIONS);
        Attribute locationAtt = new Attribute("location", locationValues);

        List<String> povValues = new ArrayList<>(2);
        Collections.addAll(povValues, POVS);
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
        // attributes.add(avgRatingsAtt);
        // attributes.add(numRatingsAtt);

        return attributes;
    }


    public void setData(String title, int year, String setting, String pov, String detective)
    {
        mData = new Instances("data", getAttributes(), 0);

        double[] values = new double[mData.numAttributes()];
        values[TITLE] = mData.attribute(0).addStringValue(title);
        values[YEAR] = year;
        values[LOCATION] = mData.attribute(2).indexOfValue(setting);
        values[POV] = mData.attribute(3).indexOfValue(pov);
        values[DETECTIVE] = mData.attribute(4).indexOfValue(detective);

        mData.add(new DenseInstance(1.0, values));

      /*  try
        {
            /* Many algorithms can't handle string attributes (book title for example)
            * so we use a filter to transform the string attribute into a nominal one. *
            StringToWordVector filter = new StringToWordVector();
            filter.setAttributeIndices("" + TITLE + 1);
            filter.setInputFormat(mData);

            // Applying the filter to the data
            mData = Filter.useFilter(mData, filter);
        }
        catch(Exception e)
        {
            Log.e("Prediction", "Exception caught", e);
        }
*/
    }


    public String classify(Context context)
    {
        KillerClassifier classifier = KillerClassifier.get(context);

        /* After applying the filter, the title attribute is moved to the end
           of the list of attributes and separated into multiple ones.
         * So the indices of other attributes move down by 1.
         */
        mLabelled = classifier.classify(mData, WEAPON - 1);

        //classifier.retrain(mLabelled.instance(0));

        return mLabelled.instance(0).classAttribute().value(0);
    }
}
