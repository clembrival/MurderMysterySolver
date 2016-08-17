package com.clemSP.iteration1.frontend;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.clemSP.iteration1.R;


/**
 * Activity containing radio buttons to select the different prototype interfaces.
 */
public class ManagerActivity extends AppCompatActivity
{
    private RadioGroup mClassGroup, mFeaturesGroup, mInputGroup, mImagesGroup;

    //private boolean mHasIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);

        //mHasIntent = getIntent() != null;

        inflateWidgets();
        //preSelectButtons();
        inflateSaveButton();
        inflateCancelButton();
    }

    private void inflateWidgets()
    {
        mClassGroup = (RadioGroup) findViewById(R.id.test_class_group);
        mFeaturesGroup = (RadioGroup) findViewById(R.id.test_features_group);
        mInputGroup = (RadioGroup) findViewById(R.id.test_input_group);
        mImagesGroup = (RadioGroup) findViewById(R.id.test_images_group);

        Button cancelButton = (Button) findViewById(R.id.test_cancel_button);
        if(cancelButton == null)
            return;

        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ManagerActivity.this.finish();
            }
        });
    }


    private void preSelectButtons()
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(
                ManagerActivity.this);

        int selectedClass = sharedPref.getInt(getString(R.string.saved_class_layout), -1);
        if(mClassGroup != null && selectedClass != -1)
            mClassGroup.check(selectedClass);

        int selectedFeatures = sharedPref.getInt(getString(R.string.saved_features_layout), -1);
        if(mFeaturesGroup != null && selectedFeatures != -1)
            mFeaturesGroup.check(selectedFeatures);

        int selectedInput = sharedPref.getInt(getString(R.string.saved_input_layout), -1);
        if(mInputGroup != null && selectedInput != -1)
            mInputGroup.check(selectedInput);

        int selectedImages = sharedPref.getInt(getString(R.string.saved_images_layout), -1);
        if(mImagesGroup != null && selectedImages != -1)
            mImagesGroup.check(selectedImages);
    }


    private void inflateSaveButton()
    {
        Button okButton = (Button) findViewById(R.id.test_save_button);
        if(okButton == null)
            return;

        okButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Save each selected option to the shared preferences.
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(
                        ManagerActivity.this);
                SharedPreferences.Editor editor = sharedPref.edit();

                int selectedClassId = mClassGroup.getCheckedRadioButtonId();
                RadioButton classButton = (RadioButton) findViewById(selectedClassId);
                if(classButton != null)
                    editor.putInt(getString(R.string.saved_class_layout), classButton.getId());
                
                int selectedFeaturesId = mFeaturesGroup.getCheckedRadioButtonId();
                RadioButton featuresButton = (RadioButton) findViewById(selectedFeaturesId);
                if(featuresButton != null)
                	editor.putInt(getString(R.string.saved_features_layout), featuresButton.getId());

                int selectedInputId = mInputGroup.getCheckedRadioButtonId();
                RadioButton inputButton = (RadioButton) findViewById(selectedInputId);
                if(inputButton != null)
                    editor.putInt(getString(R.string.saved_input_layout), inputButton.getId());

                int selectedImagesId = mImagesGroup.getCheckedRadioButtonId();
                RadioButton imagesButton = (RadioButton) findViewById(selectedImagesId);
                if(imagesButton != null)
                    editor.putInt(getString(R.string.saved_images_layout), imagesButton.getId());

                editor.apply();

                //if(!mHasIntent)
                    startActivity(new Intent(ManagerActivity.this, WelcomeActivity.class));

                ManagerActivity.this.finish();
            }
        });
    }

    private void inflateCancelButton()
    {
        Button cancelButton = (Button) findViewById(R.id.test_cancel_button);
        if(cancelButton == null)
            return;

        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ManagerActivity.this.finish();
            }
        });
    }
}
