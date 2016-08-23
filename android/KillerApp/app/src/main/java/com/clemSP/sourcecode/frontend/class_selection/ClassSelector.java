package com.clemSP.sourcecode.frontend.class_selection;

import com.clemSP.sourcecode.backend.AttributeFactory;
import com.clemSP.sourcecode.frontend.PredictionSettings;


/** Class implementing a method which updates the prediction target when the user selects it. */
public abstract class ClassSelector
{
    protected void updatePredictionTarget(boolean predictWeapon)
    {
        PredictionSettings settings = PredictionSettings.getSettings();

        int weaponIndex = AttributeFactory.AppAttribute.Weapon.getIndex();
        int murdererIndex = AttributeFactory.AppAttribute.Murderer.getIndex();

        /* If the attribute that was not the target of the prediction was (de)selected, and
         * (de)select the current prediction target. Then update the attribute to be predicted. */


        if(predictWeapon && !settings.getPredictWeapon())
        {
            settings.setFeatureIsSelected(murdererIndex, settings.getFeatureIsSelected(weaponIndex));
            settings.setFeatureIsSelected(weaponIndex, false);
        }
        else if(!predictWeapon && settings.getPredictWeapon())
        {
            settings.setFeatureIsSelected(weaponIndex, settings.getFeatureIsSelected(murdererIndex));
            settings.setFeatureIsSelected(murdererIndex, false);
        }
        settings.setPredictWeapon(predictWeapon);
    }


    /** Inner class to be used by any ClassSelector which already has a superclass. */
    public static class Adaptee extends ClassSelector {  }
}
