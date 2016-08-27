package com.clemSP.sourcecode.frontend.settings;

import android.content.Context;
import android.content.res.Resources;

import com.clemSP.sourcecode.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class PreferencesMap
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

    public static PreferencesMap sMap;

    private Map<String, EntryValue[]> mKeyPrefMap;


    public static class EntryValue
    {
        String entry, value;

        public EntryValue(String entry, String value)
        {
            this.entry = entry;
            this.value = value;
        }
    }


    private PreferencesMap(Context context)
    {
        Resources resources = context.getResources();

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
    }


    private EntryValue[] makeEntryValues(String[] entries, String[] values)
    {
        EntryValue[] entryValues = new EntryValue[entries.length];

        for(int index = 0; index < entryValues.length; index++)
            entryValues[index] = new EntryValue(entries[index], values[index]);

        return entryValues;
    }


    public static PreferencesMap getPreferencesMap(Context context)
    {
        if(sMap == null)
            sMap = new PreferencesMap(context);

        return sMap;
    }


    public Set<String> getKeySet()
    {
        return sMap.mKeyPrefMap.keySet();
    }


    public EntryValue[] getEntryValues(String key)
    {
        return sMap.mKeyPrefMap.get(key);
    }
}
