package com.clemSP.iteration1.backend;

import com.clemSP.iteration1.R;


public enum AppAttribute
{
    Year("year", 0),
    Detective("detective",1),
    Location("location", 2),
    Pov("point_of_view", 3),
    Weapon("murder_weapon", 4),
    Victim("victim_gender", 5),
    Murderer("murderer_gender", 6);
    /*AvRatings("average_ratings", 7),
    NumRatings("number_of_ratings",8);*/

    private String mLabel;
    private int mIndex;
    private static final int NUM_ATTRIBUTES = 7;


    AppAttribute(String label, int index)
    {
        mLabel = label;
        mIndex = index;
    }

    public String getLabel()
    {
        return mLabel;
    }

    public int getIndex()
    {
        return mIndex;
    }

    public static AppAttribute getAttributeFromIndex(int index)
    {
        switch(index)
        {
            case 0: return Year;
            case 1: return Detective;
            case 2: return Location;
            case 3: return Pov;
            case 4: return Weapon;
            case 5: return Victim;
            case 6: return Murderer;
        }
        return null;
    }

    public static int getLabelRes(AppAttribute attribute)
    {
        switch(attribute)
        {
            case Year: return R.string.year_label;
            case Detective: return R.string.detective_label;
            case Location: return R.string.setting_label;
            case Pov: return R.string.pov_label;
            case Weapon: return R.string.cause_label;
            case Victim: return R.string.victim_label;
            case Murderer: return R.string.murderer_label;
        }
        return -1;
    }

    public static int getErrorRes(AppAttribute attribute)
    {
        switch(attribute)
        {
            case Year: return R.string.year_error;
            case Detective: return R.string.detective_error;
            case Location: return R.string.setting_error;
            case Pov: return R.string.pov_error;
            case Weapon: return R.string.cause_error;
            case Victim: return R.string.victim_error;
            case Murderer: return R.string.gender_error;
        }
        return -1;
    }

    public static int getNumAttributes()
    {
        return NUM_ATTRIBUTES;
    }
}
