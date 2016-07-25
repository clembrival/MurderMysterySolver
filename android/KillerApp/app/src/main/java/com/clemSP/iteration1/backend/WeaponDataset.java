package com.clemSP.iteration1.backend;

import android.content.Context;

import java.util.ArrayList;

import weka.core.Attribute;
import weka.core.Instances;


public class WeaponDataset extends Dataset
{
    private static WeaponDataset sWeaponDataset;


    public static WeaponDataset get(Context context)
    {
        if(sWeaponDataset == null)
            sWeaponDataset = new WeaponDataset(context);
        return sWeaponDataset;
    }


    private WeaponDataset(Context context)
    {
        super(context, "ibk_weapon.model", WEAPON);
    }


    public static void clear()
    {
        sWeaponDataset = null;
    }


    public void setData(String title, int year, String setting, String pov, String detective)
    {
        mTitle = title;

        ArrayList<Attribute> attributes = super.getAttributes(YEAR, DETECTIVE, LOCATION, POV, WEAPON);

        mData = new Instances("data", attributes, 0);

        double[] values = new double[mData.numAttributes()];
        values[YEAR] = year;
        values[DETECTIVE] = mData.attribute(DETECTIVE).indexOfValue(detective);
        values[LOCATION] = mData.attribute(LOCATION).indexOfValue(setting);
        values[POV] = mData.attribute(POV).indexOfValue(pov);

        super.setData(values);
    }
}
