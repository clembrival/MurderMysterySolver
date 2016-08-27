package com.clemSP.sourcecode.backend;

import com.clemSP.sourcecode.R;


/** Utility class for the attributes of the dataset. */
public class AttributeFactory
{
    /** Values of the nominal attributes. */
    public static class NominalValues
    {
        public static final String[] LOCATIONS = {"unknown", "UK", "International"};
        public static final String[] POVS = {"unknown", "First", "Third"};
        public static final String[] DETECTIVES = {"unknown", "Hercule Poirot", "Tommy and Tuppence",
                "Colonel Race", "Superintendent Battle", "Miss Marple", "Mystery novel"};
        public static final String[] WEAPONS = {"unknown", "Poison", "Stabbing", "Accident", "Shooting",
                "Strangling", "Concussion", "ThroatSlit", "None", "Drowning"};
        public static final String[] VICTIMS = {"unknown", "F", "M", "None"};
        public static final String[] MURDERERS = {"unknown", "F", "M", "Lots", "None"};
    }


    /** Enumeration of the attributes, their label and their index. */
    public enum AppAttribute
    {
        Year("year", 0),
        Detective("detective",1),
        Location("location", 2),
        Pov("point_of_view", 3),
        Weapon("murder_weapon", 4),
        Victim("victim_gender", 5),
        Murderer("murderer_gender", 6),
        Rating("average_ratings", 7);

        private String mLabel;
        private int mIndex;

        /** Total number of attributes. */
        private static final int NUM_ATTRIBUTES = 8;


        AppAttribute(String label, int index)
        {
            mLabel = label;
            mIndex = index;
        }


        /**
         * @return the label of the attribute.
         */
        public String getLabel()
        {
            return mLabel;
        }

        /**
         * @return the index of the attribute.
         */
        public int getIndex()
        {
            return mIndex;
        }

        /**
         * @return the attribute with the given index.
         * @param index the index of the sought attribute.
         */
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
                case 7: return Rating;
            }
            return null;
        }

        /**
         * @return the string resource containing the label of the given attribute.
         * @param attribute the attribute of interest.
         */
        public static int getLabelRes(AppAttribute attribute)
        {
            switch(attribute)
            {
                case Year: return R.string.year_label;
                case Detective: return R.string.detective_label;
                case Location: return R.string.location_label;
                case Pov: return R.string.pov_label;
                case Weapon: return R.string.cause_label;
                case Victim: return R.string.victim_label;
                case Murderer: return R.string.murderer_label;
                case Rating: return R.string.rating_label;
            }
            return -1;
        }

        /**
         * @return the string resource containing the error message for the given attribute.
         * @param attribute the attribute of interest.
         */
        public static int getErrorRes(AppAttribute attribute)
        {
            switch(attribute)
            {
                case Year: return R.string.year_error;
                case Detective: return R.string.detective_error;
                case Location: return R.string.location_error;
                case Pov: return R.string.pov_error;
                case Weapon: return R.string.cause_error;
                case Victim: return R.string.victim_error;
                case Murderer: return R.string.gender_error;
                case Rating: return R.string.rating_error;
            }
            return -1;
        }

        /**
         * @return the id resource of the feature checkbox for the given attribute.
         * @param index the index of the attribute of interest.
         */
        public static int getCheckboxRes(int index)
        {
            switch(index)
            {
                case 0: return R.id.year_checkbox;
                case 1: return R.id.detective_checkbox;
                case 2: return R.id.location_checkbox;
                case 3: return R.id.pov_checkbox;
                case 4: return R.id.weapon_checkbox;
                case 5: return R.id.victim_checkbox;
                case 6: return R.id.murderer_checkbox;
                case 7: return R.id.rating_checkbox;
            }
            return -1;
        }

        /**
         * @return the total number of attributes.
         */
        public static int getNumAttributes()
        {
            return NUM_ATTRIBUTES;
        }
    }
}