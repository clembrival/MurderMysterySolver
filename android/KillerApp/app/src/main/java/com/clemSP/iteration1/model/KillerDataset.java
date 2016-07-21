package com.clemSP.iteration1.model;

import android.content.Context;

import java.util.ArrayList;

import weka.core.Attribute;
import weka.core.Instances;


public class KillerDataset extends Dataset
{
    private static KillerDataset sKillerDataset;


    public static KillerDataset get(Context context)
    {
        if(sKillerDataset == null)
            sKillerDataset = new KillerDataset(context);
        return sKillerDataset;
    }


    private KillerDataset(Context context)
    {
        super(context, "ibk_gender.model", MURDERER);
    }


    public static void clear()
    {
        sKillerDataset = null;
    }

    public void setData(String title, int year, String setting, String pov,
                        String detective, String weapon, String victim)
    {
        mTitle = title;

        ArrayList<Attribute> attributes = super.getAttributes(YEAR, DETECTIVE, LOCATION, POV,
                WEAPON, VICTIM, MURDERER);

        mData = new Instances("data", attributes, 0);

        double[] values = new double[mData.numAttributes()];
        values[YEAR] = year;
        values[DETECTIVE] = mData.attribute(DETECTIVE).indexOfValue(detective);
        values[LOCATION] = mData.attribute(LOCATION).indexOfValue(setting);
        values[POV] = mData.attribute(POV).indexOfValue(pov);
        values[WEAPON] = mData.attribute(WEAPON).indexOfValue(weapon);
        values[VICTIM] = mData.attribute(VICTIM).indexOfValue(victim);

        super.setData(values);
    }
}
