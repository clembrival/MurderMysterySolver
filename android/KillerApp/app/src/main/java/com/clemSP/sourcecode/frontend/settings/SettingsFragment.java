package com.clemSP.sourcecode.frontend.settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.clemSP.sourcecode.R;
import com.clemSP.sourcecode.frontend.settings.PreferencesMap.EntryValue;

import java.util.Locale;


public class SettingsFragment extends PreferenceFragment
    implements SharedPreferences.OnSharedPreferenceChangeListener
{
    private OnLanguageChangeListener mListener;

    public interface OnLanguageChangeListener
    {
        void refresh();
    }

    private PreferencesMap mPrefMap;


    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        try
        {
            mListener = (OnLanguageChangeListener) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLanguageChangeListener");
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        mPrefMap = PreferencesMap.getPreferencesMap(getActivity());

        for(String key : mPrefMap.getKeySet())
            setListPreferenceSummary(key, mPrefMap.getEntryValues(key));
    }


    private void setListPreferenceSummary(String key, EntryValue[] entryValues)
    {
        setListPreferenceSummary(findPreference(key), entryValues);
    }


    private void setListPreferenceSummary(Preference preference, EntryValue[] entryValues)
    {
        if(preference instanceof ListPreference)
        {
            ListPreference listPreference = (ListPreference) preference;
            for(EntryValue entryValue : entryValues)
            {
                if(entryValue.entry.equals(listPreference.getEntry()))
                    listPreference.setSummary(entryValue.value);
            }
        }
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        Preference preference = findPreference(key);

        switch (key)
        {
            case PreferencesMap.KEY_PREF_LANGUAGE:
                if(preference instanceof ListPreference)
                    setLanguage(((ListPreference)preference).getEntry(), sharedPreferences);
                break;

            case PreferencesMap.KEY_PREF_CLASS_LAYOUT:
            case PreferencesMap.KEY_PREF_FEATURES_LAYOUT:
            case PreferencesMap.KEY_PREF_IMAGES_LAYOUT:
                setListPreferenceSummary(preference, mPrefMap.getEntryValues(key));
                break;

            case PreferencesMap.KEY_PREF_INPUT_LAYOUT:
                if(preference instanceof ListPreference)
                {
                    enableImagePreference(getString(R.string.input_images)
                            .equals(((ListPreference)preference).getEntry()));

                    setListPreferenceSummary(preference, mPrefMap.getEntryValues(key));
                }
                break;
        }
    }


    private void setLanguage(CharSequence entry, SharedPreferences sharedPreferences)
    {
        String language;
        if(getString(R.string.language_french).equals(entry))
            language = "fr_FR";
        else
            language = "en_GB";

        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getActivity().getResources().getDisplayMetrics());

        for(String key : mPrefMap.getKeySet())
            setListPreferenceSummary(key, mPrefMap.getEntryValues(key));

        mListener.refresh();
    }


    private void enableImagePreference(boolean enable)
    {
        ListPreference imagePreference = (ListPreference) findPreference(PreferencesMap.KEY_PREF_IMAGES_LAYOUT);
        if(imagePreference != null)
            imagePreference.setEnabled(enable);
    }


    @Override
    public void onResume()
    {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onPause()
    {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }
}
