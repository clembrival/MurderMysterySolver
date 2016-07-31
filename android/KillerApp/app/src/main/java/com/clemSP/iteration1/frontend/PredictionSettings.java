package com.clemSP.iteration1.frontend;


import com.clemSP.iteration1.backend.AttributeFactory.AppAttribute;

public class PredictionSettings
{
    private static PredictionSettings sSettings;

    private boolean mPredictWeapon;
    private boolean[] mFeaturesSelected;

    private PredictionSettings() { }

    public static PredictionSettings getSettings()
    {
        if(sSettings == null)
            sSettings = new PredictionSettings();

        if(sSettings.getSelectedFeatures() == null)
            sSettings.setSelectedFeatures(new boolean[AppAttribute.getNumAttributes()]);

        return sSettings;
    }

    public boolean getPredictWeapon()
    {
        return sSettings.mPredictWeapon;
    }

    public boolean[] getSelectedFeatures()
    {
        return sSettings.mFeaturesSelected;
    }

    public int getSelectedFeaturesLength()
    {
        return sSettings.mFeaturesSelected.length;
    }

    public boolean getFeatureIsSelected(int index)
    {
        return sSettings.mFeaturesSelected[index];
    }

    public void setPredictWeapon(boolean predictWeapon)
    {
        sSettings.mPredictWeapon = predictWeapon;
    }

    public void setSelectedFeatures(boolean[] selectedFeatures)
    {
        sSettings.mFeaturesSelected = selectedFeatures;
    }

    public void setFeatureIsSelected(int index, boolean isSelected)
    {
        sSettings.mFeaturesSelected[index] = isSelected;
    }
}
