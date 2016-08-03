package com.clemSP.iteration1.frontend;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import com.clemSP.iteration1.R;
import com.clemSP.iteration1.frontend.class_selection.ClassDialog;
import com.clemSP.iteration1.frontend.class_selection.ClassTabLayout;
import com.clemSP.iteration1.frontend.features_input.BaseInputFragment;
import com.clemSP.iteration1.frontend.features_input.ImagesInputFragment;
import com.clemSP.iteration1.frontend.features_input.TextInputFragment;
import com.clemSP.iteration1.frontend.features_selection.FeatureDrawer;
import com.clemSP.iteration1.frontend.features_selection.FeatureFragment;
import com.clemSP.iteration1.frontend.prediction.GenderPredictionActivity;
import com.clemSP.iteration1.frontend.prediction.WeaponPredictionActivity;


/**
 * Activity containing the widgets for the features selected to make the prediction.
 */
public class MainActivity extends BaseActivity implements BaseInputFragment.OnFeaturesInputListener,
        FeatureFragment.SelectorListener, FeatureDrawer.FeatureDrawerListener,
        ClassTabLayout.TabLayoutListener
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

    private boolean mHasDrawerLayout;
    private boolean mIsEmpty;

    private int mPrediction;

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
        setContentView(mHasDrawerLayout ? R.layout.activity_main_drawer : R.layout.activity_main_coordinator);

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
        ClassDialog dialog = new ClassDialog(this);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                ClassDialog classDialog = (ClassDialog) dialog;

                if(!classDialog.cancelled())
                    //trainClassifier();
                    selectFeatures();
            }
        });
    }


    private void setClassTabs()
    {
        new ClassTabLayout(this);
        //trainClassifier();
        selectFeatures();
    }

/*
    private void trainClassifier()
    {
        new TrainingProcessDialog(this).execute();
    }
*/

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
        FeatureFragment selector = FeatureFragment.newInstance(this);
        selector.show(mFragmentManager, FeatureFragment.TAG);
    }


    private void setFeaturesDrawer()
    {
        new FeatureDrawer(this);

        mDrawerHintView = (TextView) findViewById(R.id.hint_textview);
        if(mDrawerHintView == null)
            return;

        layoutActivity();
    }


    private void layoutActivity()
    {
        FloatingActionButton editButton = (FloatingActionButton) findViewById(R.id.edit_button);
        if(!mHasDrawerLayout && editButton == null)
            return;

        if(mHasDrawerLayout)
        {
            if(mIsEmpty)
                mDrawerHintView.setVisibility(View.VISIBLE);
            else
                mDrawerHintView.setVisibility(View.GONE);
        }
        else
        {
            editButton.show();
            editButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    selectFeatures();
                }
            });
        }
        if(!mIsEmpty)
            layoutActivityFragment();
    }


    /* Gets the input preference and loads the appropriate layout. */
    private void layoutActivityFragment()
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
            ((BaseInputFragment)inputFragment).update();
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
            case "Accident": mPrediction = R.string.accident; break;
            case "Concussion": mPrediction = R.string.concussion; break;
            case "Drowning": mPrediction = R.string.drowning; break;
            case "Poison": mPrediction = R.string.poison; break;
            case "None": mPrediction = R.string.none; break;
            case "Shooting": mPrediction = R.string.shooting; break;
            case "Stabbing": mPrediction = R.string.stabbing; break;
            case "Strangling": mPrediction = R.string.strangling; break;
            case "ThroatSlit": mPrediction = R.string.throatslit; break;
            case "unknown": mPrediction = R.string.unknown; break;
        }

        Intent intent = new Intent(this, WeaponPredictionActivity.class);
        intent.putExtra("dataset", mPrediction);

        startActivityForResult(intent, WEAPON_PREDICTION_REQUEST_CODE);
    }


    private void predictGender(String label)
    {
        switch (label)
        {
            case "F": mPrediction = R.string.female; break;
            case "M": mPrediction = R.string.male; break;
            case "Lots": mPrediction = R.string.lots; break;
            case "unknown": mPrediction = R.string.unknown; break;
        }

        Intent intent = new Intent(this, GenderPredictionActivity.class);
        intent.putExtra("dataset", mPrediction);

        startActivityForResult(intent, GENDER_PREDICTION_REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode == RESULT_OK)
        {
            mReturningActivity = requestCode;

            if(requestCode == CLASS_REQUEST_CODE)
            {
                mSettings.setPredictWeapon(data.getExtras().getBoolean("weapon"));
            }
            else if(requestCode == GENDER_PREDICTION_REQUEST_CODE
                    || requestCode == WEAPON_PREDICTION_REQUEST_CODE)
            {
                layoutActivityFragment();
            }
            else
            {
                mReturningActivity = -1;
            }
        }
        else
            mReturningActivity = -1;
    }
    
    
    @Override
    public void onResume() 
    {
    	super.onResume();
    	
    	if(mReturningActivity == CLASS_REQUEST_CODE)
    		selectFeatures();
    }


    @Override
    public void onOkButtonPressed()
    {
        mIsEmpty = false;
        layoutActivity();
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
                inputFragment.update();
            else
                layoutActivity();

        }
    }


    @Override
    public void onTabSelected()
    {
        if(!mIsEmpty)
            layoutActivityFragment();
    }
}
