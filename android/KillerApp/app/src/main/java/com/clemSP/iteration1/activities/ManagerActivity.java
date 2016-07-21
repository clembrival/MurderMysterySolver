package com.clemSP.iteration1.activities;

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
    private RadioGroup mClassGroup, mInputGroup, mImagesGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);

        inflateWidgets();
        inflateSaveButton();
        inflateCancelButton();
    }

    private void inflateWidgets()
    {
        mClassGroup = (RadioGroup) findViewById(R.id.test_class_group);
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

                int selectedInputId = mInputGroup.getCheckedRadioButtonId();
                RadioButton inputButton = (RadioButton) findViewById(selectedInputId);
                if(inputButton != null)
                    editor.putInt(getString(R.string.saved_input_layout), inputButton.getId());

                int selectedImagesId = mImagesGroup.getCheckedRadioButtonId();
                RadioButton imagesButton = (RadioButton) findViewById(selectedImagesId);
                if(imagesButton != null)
                    editor.putInt(getString(R.string.saved_images_layout), imagesButton.getId());

                editor.apply();

                startActivity(new Intent(ManagerActivity.this, MainActivity.class));
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
