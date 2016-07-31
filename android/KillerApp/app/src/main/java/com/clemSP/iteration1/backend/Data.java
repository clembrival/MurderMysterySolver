package com.clemSP.iteration1.backend;

import com.clemSP.iteration1.backend.AttributeFactory.AppAttribute;

public class Data
{
    private static final int TITLE = 0, YEAR = AppAttribute.Year.getIndex() + 1,
            DETECTIVE = AppAttribute.Detective.getIndex() + 1,
            SETTING = AppAttribute.Location.getIndex() + 1,
            POV = AppAttribute.Pov.getIndex() + 1,
            WEAPON = AppAttribute.Weapon.getIndex() + 1,
            VICTIM = AppAttribute.Victim.getIndex() + 1,
            MURDERER = AppAttribute.Murderer.getIndex() + 1,
            RATING = AppAttribute.Rating.getIndex() + 1;

    private String[] mData;


    public Data()
    {
        mData = new String[AppAttribute.getNumAttributes() + 1];
    }

    public String getTitle()
    {
        return mData[TITLE];
    }

    public void setTitle(String title)
    {
        mData[TITLE] = title;
    }

    public String getYear()
    {
        return mData[YEAR];
    }

    public void setYear(String year)
    {
        mData[YEAR] = year;
    }

    public String getPov()
    {
        return mData[POV];
    }

    public void setPov(String pov)
    {
        mData[POV] = pov;
    }

    public String getDetective()
    {
        return mData[DETECTIVE];
    }

    public void setDetective(String detective)
    {
        mData[DETECTIVE] = detective;
    }

    public String getVictim()
    {
        return mData[VICTIM];
    }

    public void setVictim(String victim)
    {
        mData[VICTIM] = victim;
    }

    public String getWeapon()
    {
        return mData[WEAPON];
    }

    public void setWeapon(String weapon)
    {
        mData[WEAPON] = weapon;
    }

    public String getGender()
    {
        return mData[MURDERER];
    }

    public void setGender(String gender)
    {
        this.mData[WEAPON] = gender;
    }

    public String getSetting()
    {
        return mData[SETTING];
    }

    public void setSetting(String setting)
    {
        mData[SETTING] = setting;
    }

    public String getRating()
    {
        return mData[RATING];
    }

    public void setRating(String rating)
    {
        mData[RATING] = rating;
    }


    public void setAttribute(AppAttribute attribute, String value)
    {
        switch (attribute)
        {
            case Year: setYear(value);
            case Detective: setDetective(value); break;
            case Location: setSetting(value); break;
            case Pov: setPov(value);
            case Weapon: setWeapon(value); break;
            case Victim: setVictim(value); break;
            case Murderer: setGender(value); break;
            case Rating: setRating(value); break;
        }
    }


    public String getValue(int index)
    {
        switch (index)
        {
            case 0: return getTitle();
            case 1: return getYear();
            case 2: return getDetective();
            case 3: return getSetting();
            case 4: return getPov();
            case 5: return getWeapon();
            case 6: return getVictim();
            case 7: return getGender();
            case 8: return getRating();
        }
        return null;
    }


    public void setOthers()
    {
        for(int index = 0; index < mData.length; index++)
            if(mData[index] == null)
                mData[index] = "unknown";
    }
}
