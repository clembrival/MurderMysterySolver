package com.clemSP.sourcecode.backend;

import com.clemSP.sourcecode.backend.AttributeFactory.AppAttribute;


/** Class to store the user input for a prediction. */
public class Data
{
    /* The title is not included in the list of AppAttributes,
     * which starts at 0 with the year of publication,
     * so add 1 to all the AppAttributes indices. */
    private static final int TITLE = 0, 
    		YEAR = AppAttribute.Year.getIndex() + 1,
            DETECTIVE = AppAttribute.Detective.getIndex() + 1,
            LOCATION = AppAttribute.Location.getIndex() + 1,
            POV = AppAttribute.Pov.getIndex() + 1,
            WEAPON = AppAttribute.Weapon.getIndex() + 1,
            VICTIM = AppAttribute.Victim.getIndex() + 1,
            MURDERER = AppAttribute.Murderer.getIndex() + 1,
            RATING = AppAttribute.Rating.getIndex() + 1;

    /** Array containing the input values for all the attributes. */
    private String[] mData;


    public Data()
    {
        mData = new String[AppAttribute.getNumAttributes() + 1];
    }

    /**
     * @return the input title (or "unknown" if there were none).
     */
    public String getTitle()
    {
        return mData[TITLE];
    }

    /**
     * Sets the title to the given string.
     * @param title the input title.
     */
    public void setTitle(String title)
    {
        mData[TITLE] = title;
    }


    /**
     * @return the input year (or "unknown" if there were none).
     */
    public String getYear()
    {
        return mData[YEAR];
    }

    /**
     * Sets the year of publication to the given string.
     * @param year the input year of publication.
     */
    public void setYear(String year)
    {
        mData[YEAR] = year;
    }


    /**
     * @return the input detective (or "unknown" if there were none).
     */
    public String getDetective()
    {
        return mData[DETECTIVE];
    }

    /**
     * Sets the detective to the given string.
     * @param detective the input detective.
     */
    public void setDetective(String detective)
    {
        mData[DETECTIVE] = detective;
    }


    /**
     * @return the input location (or "unknown" if there were none).
     */
    public String getLocation()
    {
        return mData[LOCATION];
    }

    /**
     * Sets the location to the given string.
     * @param location the input location.
     */
    public void setLocation(String location)
    {
        mData[LOCATION] = location;
    }


    /**
     * @return the input point of view (or "unknown" if there were none).
     */
    public String getPov()
    {
        return mData[POV];
    }

    /**
     * Sets the point of view to the given string.
     * @param pov the input point of view.
     */
    public void setPov(String pov)
    {
        mData[POV] = pov;
    }


    /**
     * @return the input cause of death (or "unknown" if there were none).
     */
    public String getWeapon()
    {
        return mData[WEAPON];
    }

    /**
     * Sets the cause of death to the given string.
     * @param weapon the input cause of death.
     */
    public void setWeapon(String weapon)
    {
        mData[WEAPON] = weapon;
    }


    /**
     * @return the input victim's gender (or "unknown" if there were none).
     */
    public String getVictim()
    {
        return mData[VICTIM];
    }

    /**
     * Sets the gender of the victim to the given string.
     * @param victim the input victim's gender.
     */
    public void setVictim(String victim)
    {
        mData[VICTIM] = victim;
    }


    /**
     * @return the input murderer's gender (or "unknown" if there were none).
     */
    public String getGender()
    {
        return mData[MURDERER];
    }

    /**
     * Sets the gender of the murderer to the given string.
     * @param gender the input murderer's gender.
     */
    public void setGender(String gender)
    {
        this.mData[MURDERER] = gender;
    }


    /**
     * @return the input book's rating (or "unknown" if there were none).
     */
    public String getRating()
    {
        return mData[RATING];
    }

    /**
     * Sets the rating of the book to the given string.
     * @param rating the input book's rating.
     */
    public void setRating(String rating)
    {
        mData[RATING] = rating;
    }


    /**
     * @return the value of the attribute with the given index.
     * @param index the index of the attribute of interest.
     */
    public String getAttributeValue(int index)
    {
    	return mData[index];
    }


    /**
     * Sets the given attribute to the given string.
     * @param attribute the attribute of interest.
     * @param value the input value for the given attribute.
     */
    public void setAttribute(AppAttribute attribute, String value)
    {
        switch (attribute)
        {
            case Year: setYear(value);
            case Detective: setDetective(value); break;
            case Location: setLocation(value); break;
            case Pov: setPov(value);
            case Weapon: setWeapon(value); break;
            case Victim: setVictim(value); break;
            case Murderer: setGender(value); break;
            case Rating: setRating(value); break;
        }
    }


    /**
     * @return the value of the attribute with the given index.
     * @param index the index of the attribute of interest.
     */
    public String getValue(int index)
    {
        switch (index)
        {
            case 0: return getTitle();
            case 1: return getYear();
            case 2: return getDetective();
            case 3: return getLocation();
            case 4: return getPov();
            case 5: return getWeapon();
            case 6: return getVictim();
            case 7: return getGender();
            case 8: return getRating();
        }
        return null;
    }


    /**
     * Sets all the unset attribute to "unknown".
     */
    public void setOthers()
    {
        for(int index = 0; index < mData.length; index++)
            if(mData[index] == null)
                mData[index] = "unknown";
    }


    /**
     * @return a string containing the value of all the attributes, separated by commas.
     */
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        for(int index = 0; index < AppAttribute.getNumAttributes()+1; index++)
        {
            builder.append(getValue(index));
            if(index < AppAttribute.getNumAttributes())
                builder.append(", ");
        }
        return builder.toString();
    }
}