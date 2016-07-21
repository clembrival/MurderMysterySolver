package com.clemSP.iteration1.model;

import android.content.Context;

import java.util.ArrayList;

import weka.core.Attribute;
import weka.core.Instances;


public class VariableDataset extends Dataset
{
    private static VariableDataset sVariableDataset;


    public static VariableDataset get(Context context, boolean predictWeapon)
    {
        if(sVariableDataset == null)
        {
            String model = predictWeapon ? "ibk_weapon.model" : "ibk_gender.model";
            int classIndex = predictWeapon ? WEAPON : MURDERER;

            sVariableDataset = new VariableDataset(context, model, classIndex);
        }
        return sVariableDataset;
    }


    private VariableDataset(Context context, String model, int classIndex)
    {
        super(context, model, classIndex);
    }


    public static void clear()
    {
        sVariableDataset = null;
    }


    public void setData(String title, int year, String pov, String detective, String victim,
                        String weapon, String murderer, String setting)
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
        values[MURDERER] = mData.attribute(MURDERER).indexOfValue(murderer);

        super.setData(values);
    }
}
