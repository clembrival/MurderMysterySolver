package com.clemSP.iteration1.frontend;

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
import android.view.View;
import android.widget.TextView;

import com.clemSP.iteration1.R;
import com.clemSP.iteration1.frontend.class_selection.ClassDialog;
import com.clemSP.iteration1.frontend.class_selection.ClassTabLayout;
import com.clemSP.iteration1.frontend.features_input.BaseInputFragment;
import com.clemSP.iteration1.frontend.features_input.ImagesInputFragment;
import com.clemSP.iteration1.frontend.features_input.TextInputFragment;
import com.clemSP.iteration1.frontend.features_selection.FeatureDialog;
import com.clemSP.iteration1.frontend.features_selection.FeatureDrawer;
import com.clemSP.iteration1.frontend.prediction.GenderPredictionActivity;
import com.clemSP.iteration1.frontend.prediction.WeaponPredictionActivity;


/**
 * Activity containing the widgets for the features selected to make the prediction.
 */
@SuppressWarnings("serial")
public class MainActivity extends BaseActivity implements BaseInputFragment.OnFeaturesInputListener,
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

    private TextView mDrawerHintView;


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

        mHasDrawerLayout = mSharedPref.getInt(getString(R.string.saved_features_layout),
                R.id.test_features_drawer) == R.id.test_features_drawer;
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
        int classLayout = mSharedPref.getInt(getString(R.string.saved_class_layout),
                R.id.test_class_dialog);

        if(classLayout == R.id.test_class_dialog)
            setClassDialog();
        else if(classLayout == R.id.test_class_tabs)
            setClassTabs();
        // TODO setClassMenuItem
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
    	int featuresLayout = mSharedPref.getInt(getString(R.string.saved_features_layout), 
    			R.id.test_features_dialog);

        if(featuresLayout == R.id.test_features_dialog)
            setFeaturesDialog();
        else if(featuresLayout == R.id.test_features_drawer)
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

        mDrawerHintView = (TextView) findViewById(R.id.hint_textview);
        if(mDrawerHintView == null)
            return;

        layoutActivity();
    }


    private void layoutActivity()
    {
        showFloatingButton();

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

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

            if(mIsEmpty)
                mDrawerHintView.setVisibility(View.VISIBLE);
            else
                mDrawerHintView.setVisibility(View.GONE);
        }
        else
        {
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
            int inputType = mSharedPref.getInt(getString(R.string.saved_input_layout),
                    R.id.test_images_button);

            if(inputType == R.id.test_images_button)
            {
                ImagesInputFragment imagesInputFragment = new ImagesInputFragment();
                mFragmentManager.beginTransaction().add(R.id.fragment_container, imagesInputFragment).commit();
            }
            else
            {
                TextInputFragment textInputFragment = new TextInputFragment();
                mFragmentManager.beginTransaction().add(R.id.fragment_container, textInputFragment).commit();
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
    public void onResume() 
    {
    	super.onResume();
    	
    	if(mReturningActivity == CLASS_REQUEST_CODE)
    		selectFeatures();
    }


    @Override
    public void onFeatureDrawerClosed(boolean selectionIsValid)
    {
        if(!selectionIsValid)
            printErrorToast(R.string.feature_error);
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
}
