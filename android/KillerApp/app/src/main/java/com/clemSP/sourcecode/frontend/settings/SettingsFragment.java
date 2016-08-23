package com.clemSP.sourcecode.frontend.settings;


import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.clemSP.sourcecode.R;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SettingsFragment extends PreferenceFragment
    implements SharedPreferences.OnSharedPreferenceChangeListener
{
    public static final String KEY_PREF_LANGUAGE = "pref_language";
    public static final String KEY_PREF_CLASS_LAYOUT = "pref_classLayout";
    public static final String KEY_PREF_FEATURES_LAYOUT = "pref_featuresLayout";
    public static final String KEY_PREF_INPUT_LAYOUT = "pref_inputLayout";
    public static final String KEY_PREF_IMAGES_LAYOUT = "pref_imagesLayout";
    public static final String KEY_PREF_SHARE_DATA = "pref_shareData";
    public static final String KEY_PREF_SEND_DATA_AUTO = "pref_sendDataAuto";
    public static final String KEY_PREF_FETCH_DATA_AUTO = "pref_fetchDataAuto";
    public static final String KEY_PREF_CORRECT_ANSWER_AUTO = "pref_correctAnswerAuto";

    private Map<String, EntryValue[]> mKeyPrefMap;

    private class EntryValue
    {
        String entry, value;

        public EntryValue(String entry, String value)
        {
            this.entry = entry;
            this.value = value;
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        Resources resources = getResources();

        mKeyPrefMap = new HashMap<>(4);

        mKeyPrefMap.put(KEY_PREF_CLASS_LAYOUT, makeEntryValues(
                resources.getStringArray(R.array.pref_classLayout_entries),
                resources.getStringArray(R.array.pref_classLayout_values)));

        mKeyPrefMap.put(KEY_PREF_FEATURES_LAYOUT, makeEntryValues(
                resources.getStringArray(R.array.pref_featuresLayout_entries),
                resources.getStringArray(R.array.pref_featuresLayout_values)));

        mKeyPrefMap.put(KEY_PREF_INPUT_LAYOUT, makeEntryValues(
                resources.getStringArray(R.array.pref_inputLayout_entries),
                resources.getStringArray(R.array.pref_inputLayout_values)));

        mKeyPrefMap.put(KEY_PREF_IMAGES_LAYOUT, makeEntryValues(
                resources.getStringArray(R.array.pref_imagesLayout_entries),
                resources.getStringArray(R.array.pref_imagesLayout_values)));

        for(String key : mKeyPrefMap.keySet())
            setListPreferenceSummary(key, mKeyPrefMap.get(key));
    }


    private EntryValue[] makeEntryValues(String[] entries, String[] values)
    {
        EntryValue[] entryValues = new EntryValue[entries.length];

        for(int index = 0; index < entryValues.length; index++)
            entryValues[index] = new EntryValue(entries[index], values[index]);

        return entryValues;
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
            case KEY_PREF_LANGUAGE:
                if(preference instanceof ListPreference)
                    setLanguage(((ListPreference)preference).getEntry());
                break;

            case KEY_PREF_CLASS_LAYOUT:
            case KEY_PREF_FEATURES_LAYOUT:
            case KEY_PREF_IMAGES_LAYOUT:
                setListPreferenceSummary(preference, mKeyPrefMap.get(key));
                break;

            case KEY_PREF_INPUT_LAYOUT:
                if(preference instanceof ListPreference)
                {
                    enableImagePreference(getString(R.string.input_images)
                            .equals(((ListPreference)preference).getEntry()));

                    setListPreferenceSummary(preference, mKeyPrefMap.get(key));
                }
                break;
        }
    }


    private void setLanguage(CharSequence entry)
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
        getResources().updateConfiguration(config, null);
    }


    private void enableImagePreference(boolean enable)
    {
        ListPreference imagePreference = (ListPreference) findPreference(KEY_PREF_IMAGES_LAYOUT);
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
}
