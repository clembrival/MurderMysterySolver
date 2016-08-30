package com.clemSP.sourcecode.frontend.settings;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.clemSP.sourcecode.R;
import com.clemSP.sourcecode.frontend.MainActivity;


public class SettingsActivity extends AppCompatActivity
{
    private static final String FRAGMENT_TAG = "settingsFrag";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment(), FRAGMENT_TAG).commit();

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem mainItem = menu.findItem(R.id.action_main);
        if(mainItem != null)
            mainItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        MenuItem settingsItem = menu.findItem(R.id.action_settings);
        if(settingsItem != null)
            settingsItem.setVisible(false);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        switch(id)
        {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.action_main:
                startActivity(new Intent(this, MainActivity.class));
                finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
