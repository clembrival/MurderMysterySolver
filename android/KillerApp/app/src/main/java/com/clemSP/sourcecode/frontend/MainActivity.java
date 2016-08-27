package com.clemSP.sourcecode.frontend;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.clemSP.sourcecode.R;
import com.clemSP.sourcecode.frontend.class_selection.ClassDialog;
import com.clemSP.sourcecode.frontend.class_selection.ClassTabLayout;
import com.clemSP.sourcecode.frontend.features_input.BaseInputFragment;
import com.clemSP.sourcecode.frontend.features_input.ImagesInputFragment;
import com.clemSP.sourcecode.frontend.features_input.TextInputFragment;
import com.clemSP.sourcecode.frontend.features_selection.FeatureDialog;
import com.clemSP.sourcecode.frontend.features_selection.FeatureDrawer;
import com.clemSP.sourcecode.frontend.prediction.GenderPredictionActivity;
import com.clemSP.sourcecode.frontend.prediction.WeaponPredictionActivity;
import com.clemSP.sourcecode.frontend.settings.PreferencesMap;
import com.clemSP.sourcecode.frontend.settings.SettingsActivity;


/**
 * Activity containing the widgets for the features selected to make the prediction.
 */
@SuppressWarnings("serial")
public class MainActivity extends AppCompatActivity implements BaseInputFragment.OnFeaturesInputListener,
        FeatureDrawer.FeatureDrawerListener, ClassTabLayout.TabLayoutListener
{
    /** Request codes for other activities started by this activity. */
    private static final int CLASS_REQUEST_CODE = 0;
    private static final int WEAPON_PREDICTION_REQUEST_CODE = 1;
    private static final int GENDER_PREDICTION_REQUEST_CODE = 2;

    /** Request code of the last activity that finished before this activity resumed. */
    private int mReturningActivity;

    private SharedPreferences mSharedPref;
    private FragmentManager mFragmentManager;

    private PredictionSettings mSettings;

    private boolean mHasDrawerLayout, mHasTabLayout, mIsEmpty;

    private int mPredictionRes;


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem settingsItem = menu.findItem(R.id.action_settings);
        if(settingsItem != null)
            settingsItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        MenuItem mainItem = menu.findItem(R.id.action_main);
        if(mainItem != null)
            mainItem.setVisible(false);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        switch(id)
        {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mReturningActivity = -1;
        mIsEmpty = true;

        mSettings = PredictionSettings.getSettings();
        mSettings.setPredictWeapon(true);

        mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        mFragmentManager = getSupportFragmentManager();

        setContentView(R.layout.activity_main);

        if(findViewById(R.id.fragment_container) == null)
            return;

        if (savedInstanceState != null)
            return;

        selectClass();
    }


    /* Gets the class preference and loads the appropriate layout. */
    private void selectClass()
    {
        String classLayout = mSharedPref.getString(PreferencesMap.KEY_PREF_CLASS_LAYOUT,
                getString(R.string.pref_classDialog));

        if(getString(R.string.pref_classDialog).equals(classLayout))
            setClassDialog();
        else if(getString(R.string.pref_classTabs).equals(classLayout))
            setClassTabs();
    }


    private void setClassDialog()
    {
        mHasTabLayout = false;

        ClassDialog dialog = new ClassDialog(this);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                ClassDialog classDialog = (ClassDialog) dialog;

                if(!classDialog.cancelled())
                {
                    if(mIsEmpty)
                        selectFeatures();
                    else
                        layoutActivityFragment(false);
                }
                else
                    layoutActivity();
            }
        });
    }


    private void setClassTabs()
    {
        mHasTabLayout = true;

        new ClassTabLayout(this);

        if(mIsEmpty)
            selectFeatures();
    }


    private void selectFeatures()
    {
        String featuresLayout = mSharedPref.getString(PreferencesMap.KEY_PREF_FEATURES_LAYOUT,
                getString(R.string.pref_featuresDialog));

        if(getString(R.string.pref_featuresDialog).equals(featuresLayout))
            setFeaturesDialog();
        else if(getString(R.string.pref_featuresDrawer).equals(featuresLayout))
            setFeaturesDrawer();
    }


    /* Opens a dialog for the user to select the features to be used for the prediction. */
    private void setFeaturesDialog()
    {
        mHasDrawerLayout = false;

        FeatureDialog dialog = new FeatureDialog(this, R.layout.dialog_features,
                !mHasTabLayout && !mIsEmpty);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                FeatureDialog featureDialog = (FeatureDialog) dialog;

                if(!featureDialog.cancelled())
                    mIsEmpty = false;

                layoutActivity();
            }
        });
    }


    private void setFeaturesDrawer()
    {
        mHasDrawerLayout = true;

        new FeatureDrawer(this);

        layoutActivity();
    }


    private void layoutActivity()
    {
        showFloatingButton();

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        TextView drawerHintView = (TextView) findViewById(R.id.hint_textview);

        if(mHasDrawerLayout)
        {
            if(drawerLayout != null)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            if(!mHasTabLayout)
            {
                TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
                if(tabLayout != null)
                    tabLayout.setVisibility(View.GONE);
            }

            if(drawerHintView != null)
            {
                if(mIsEmpty)
                    drawerHintView.setVisibility(View.VISIBLE);
                else
                    drawerHintView.setVisibility(View.GONE);
            }
        }
        else
        {
            if(drawerHintView != null)
                drawerHintView.setVisibility(View.GONE);

            if(drawerLayout != null)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }

        if(!mIsEmpty)
            layoutActivityFragment(false);
    }


    private void showFloatingButton()
    {
        FloatingActionButton editButton = (FloatingActionButton) findViewById(R.id.edit_button);
        if(editButton == null)
            return;

        if(!mHasTabLayout || !mHasDrawerLayout)
        {
            editButton.show();

            editButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (!mHasDrawerLayout)
                        selectFeatures();
                    else
                        selectClass();
                }
            });
        }
        else
            editButton.hide();
    }


    /* Gets the input preference and loads the appropriate layout. */
    private void layoutActivityFragment(boolean clear)
    {
        Fragment inputFragment = mFragmentManager.findFragmentById(R.id.fragment_container);

        if(inputFragment == null)
        {
            String inputType = mSharedPref.getString(PreferencesMap.KEY_PREF_INPUT_LAYOUT,
                    getString(R.string.pref_inputText));

            if(getString(R.string.pref_inputText).equals(inputType))
            {
                TextInputFragment textInputFragment = new TextInputFragment();
                mFragmentManager.beginTransaction().add(R.id.fragment_container, textInputFragment).commit();
            }
            else
            {
                ImagesInputFragment imagesInputFragment = new ImagesInputFragment();
                mFragmentManager.beginTransaction().add(R.id.fragment_container, imagesInputFragment).commit();
            }
            mIsEmpty = false;
        }
        else
            ((BaseInputFragment)inputFragment).update(clear);
    }


    @Override
    public void onFeaturesInput(String label)
    {
        if(mSettings.getPredictWeapon())
            predictWeapon(label);
        else
            predictGender(label);
    }


    private void predictWeapon(String label)
    {
        switch (label)
        {
            case "Accident": mPredictionRes = R.string.accident; break;
            case "Concussion": mPredictionRes = R.string.concussion; break;
            case "Drowning": mPredictionRes = R.string.drowning; break;
            case "Poison": mPredictionRes = R.string.poison; break;
            case "None": mPredictionRes = R.string.none; break;
            case "Shooting": mPredictionRes = R.string.shooting; break;
            case "Stabbing": mPredictionRes = R.string.stabbing; break;
            case "Strangling": mPredictionRes = R.string.strangling; break;
            case "ThroatSlit": mPredictionRes = R.string.throatslit; break;
            case "unknown": mPredictionRes = R.string.unknown; break;
        }

        Intent intent = new Intent(this, WeaponPredictionActivity.class);
        intent.putExtra("dataset", mPredictionRes);

        startActivityForResult(intent, WEAPON_PREDICTION_REQUEST_CODE);
    }


    private void predictGender(String label)
    {
        switch (label)
        {
            case "F": mPredictionRes = R.string.female; break;
            case "M": mPredictionRes = R.string.male; break;
            case "Lots": mPredictionRes = R.string.lots; break;
            case "unknown": mPredictionRes = R.string.unknown; break;
        }

        Intent intent = new Intent(this, GenderPredictionActivity.class);
        intent.putExtra("dataset", mPredictionRes);

        startActivityForResult(intent, GENDER_PREDICTION_REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode == RESULT_OK)
        {
            mReturningActivity = requestCode;

            switch (requestCode)
            {
                case CLASS_REQUEST_CODE:
                    mSettings.setPredictWeapon(data.getExtras().getBoolean("weapon"));
                    break;

                case GENDER_PREDICTION_REQUEST_CODE:
                case WEAPON_PREDICTION_REQUEST_CODE:
                    layoutActivityFragment(true);
                    break;

                default:
                    mReturningActivity = -1;
                    break;
            }
        }
        else
        {
            mReturningActivity = -1;
        }
    }


    @Override
    public void onFeatureDrawerClosed(boolean selectionIsValid)
    {
        if(!selectionIsValid)
            Toast.makeText(this, R.string.feature_error, Toast.LENGTH_SHORT).show();
        else
        {
            mIsEmpty = false;

            BaseInputFragment inputFragment = (BaseInputFragment) mFragmentManager
                    .findFragmentById(R.id.fragment_container);

            if(inputFragment != null)
                inputFragment.update(false);
            else
                layoutActivity();

        }
    }


    @Override
    public void onTabSelected()
    {
        if(!mIsEmpty)
            layoutActivityFragment(false);
    }


    @Override
    public void onResume()
    {
        super.onResume();

        if(mReturningActivity == CLASS_REQUEST_CODE)
            selectFeatures();
    }
}
