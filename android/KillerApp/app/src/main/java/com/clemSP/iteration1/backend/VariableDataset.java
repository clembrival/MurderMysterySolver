package com.clemSP.iteration1.backend;

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


    public void setData(Data data)
    {
        mTitle = data.getTitle();

        ArrayList<Attribute> attributes = super.getAttributes(YEAR, DETECTIVE, LOCATION, POV,
                WEAPON, VICTIM, MURDERER);

        mData = new Instances("data", attributes, 0);

        double[] values = new double[mData.numAttributes()];

        values[YEAR] = Integer.parseInt(data.getYear());
        values[POV] = mData.attribute(POV).indexOfValue(data.getPov());
        values[DETECTIVE] = mData.attribute(DETECTIVE).indexOfValue(data.getDetective());
        values[VICTIM] = mData.attribute(VICTIM).indexOfValue(data.getVictim());
        values[WEAPON] = mData.attribute(WEAPON).indexOfValue(data.getWeapon());
        values[MURDERER] = mData.attribute(MURDERER).indexOfValue(data.getGender());
        values[LOCATION] = mData.attribute(LOCATION).indexOfValue(data.getSetting());

        super.setData(values);
    }
}
