package com.clemSP.iteration1.model;

/**
 * Created by Clem on 09/07/2016.
 */
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

    public static int getNumAttributes()
    {
        return NUM_ATTRIBUTES;
    }
}
