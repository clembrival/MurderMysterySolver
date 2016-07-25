package com.clemSP.iteration1.frontend;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import com.clemSP.iteration1.R;
import com.clemSP.iteration1.backend.AppAttribute;
import com.clemSP.iteration1.frontend.class_selection.ClassDialog;
import com.clemSP.iteration1.frontend.class_selection.ClassMenuActivity;
import com.clemSP.iteration1.frontend.features_input.BaseInputFragment;
import com.clemSP.iteration1.frontend.features_input.ImagesInputFragment;
import com.clemSP.iteration1.frontend.features_input.TextInputFragment;
import com.clemSP.iteration1.frontend.features_selection.FeatureDrawer;
import com.clemSP.iteration1.frontend.features_selection.FeaturesDialog;
import com.clemSP.iteration1.frontend.prediction.GenderPredictionActivity;
import com.clemSP.iteration1.frontend.prediction.WeaponPredictionActivity;


/**
 * Activity containing the widgets for the features selected to make the prediction.
 */
public class MainActivity extends BaseActivity implements BaseInputFragment.OnFeaturesInputListener
{
    private static final int CLASS_REQUEST_CODE = 0;
    private static final int WEAPON_PREDICTION_REQUEST_CODE = 1;
    private static final int GENDER_PREDICTION_REQUEST_CODE = 2;

    private boolean mPredictWeapon;
    private boolean[] mSelectedFeatures;
    private int mPrediction;

    private SharedPreferences mSharedPref;

    private FragmentManager mFragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(findViewById(R.id.fragment_container) == null)
            return;

        if (savedInstanceState != null)
            return;

        mSelectedFeatures = new boolean[AppAttribute.getNumAttributes()];

        mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        mFragmentManager = getSupportFragmentManager();

        selectClass();
    }


    /* Gets the class preference and loads the appropriate layout. */
    private void selectClass()
    {
        int classLayout = mSharedPref.getInt(getString(R.string.saved_class_layout),
                R.id.test_dialog_button);

        if(classLayout == R.id.test_dialog_button)
            setClassDialog();
        else
            setClassMenu();
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
                {
                    mPredictWeapon = classDialog.getPredictWeapon();
                    setFeaturesDialog();
                }
            }
        });
    }


    private void setClassMenu()
    {
        startActivityForResult(new Intent(this, ClassMenuActivity.class), CLASS_REQUEST_CODE);
    }


    /* Opens a dialog for the user to select the features to be used for the prediction. */
    private void setFeaturesDialog()
    {
        FeaturesDialog dialog = new FeaturesDialog(this, mPredictWeapon);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                FeaturesDialog featuresDialog = (FeaturesDialog) dialog;

                if(!featuresDialog.cancelled())
                {
                    mSelectedFeatures = ((FeaturesDialog) dialog).getSelectedFeatures();
                    String test = "";
                    for(boolean b : mSelectedFeatures)
                        test += "" + b;
                    Toast.makeText(MainActivity.this, test, Toast.LENGTH_LONG).show();
                    layoutActivityDrawer();
                }
                else
                    selectClass();
            }
        });
    }


    private void layoutActivityDrawer()
    {
        FeatureDrawer drawer = new FeatureDrawer(this, mSelectedFeatures, mPredictWeapon);

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
                imagesInputFragment.setPredictWeapon(mPredictWeapon);
                imagesInputFragment.setSelectedFeatures(mSelectedFeatures);
                mFragmentManager.beginTransaction().add(R.id.fragment_container, imagesInputFragment).commit();
            }
            else
            {
                TextInputFragment textInputFragment = new TextInputFragment();
                textInputFragment.setPredictWeapon(mPredictWeapon);
                textInputFragment.setSelectedFeatures(mSelectedFeatures);
                mFragmentManager.beginTransaction().add(R.id.fragment_container, textInputFragment).commit();
            }
        }
    }


    @Override
    public void onFeaturesInput(String label)
    {
        if(mPredictWeapon)
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
        if(requestCode == CLASS_REQUEST_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                mPredictWeapon = data.getExtras().getBoolean("weapon");
                setFeaturesDialog();
            }
        }
    }
}
