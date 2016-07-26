package com.clemSP.iteration1.frontend;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.clemSP.iteration1.R;
import com.clemSP.iteration1.backend.KillerDataset;
import com.clemSP.iteration1.frontend.features_input.YearDialog;
import com.clemSP.iteration1.frontend.prediction.GenderPredictionActivity;


/**
 * Activity with text-based widgets with all first 6 features to predict the gender of the killer.
 */
public class GenderActivity extends BaseActivity
{
    private EditText mTitleField;
    private Spinner mDetectiveSpin, mCauseSpin, mVictimSpin;
    private RadioGroup mSettingGroup, mPovGroup;
    private Button mYearButton;

    private int mYear;
    private int mPrediction;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gender);

        inflateWidgets();

        mDetectiveSpin = inflateSpinner(R.array.detective_array, R.id.detective_spinner);
        mCauseSpin = inflateSpinner(R.array.cause_array, R.id.cause_spinner);
        mVictimSpin = inflateSpinner(R.array.gender_array, R.id.victim_spinner);

        inflateYearButton();
        inflateDetectButton();
    }


    private void inflateWidgets()
    {
        mTitleField = (EditText)findViewById(R.id.title_textfield);
        if(mTitleField == null)
            return;

        mTitleField.setHint(R.string.title_label);

        mSettingGroup = (RadioGroup)findViewById(R.id.setting_group);
        mPovGroup = (RadioGroup)findViewById(R.id.pov_group);
    }


    private Spinner inflateSpinner(int entriesArray, int spinnerRes)
    {
        // String array containing the different entries
        String[] entries = getResources().getStringArray(entriesArray);

        // Adding entries and custom layout to spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_layout, entries);

        // Setting layout of the drop down menu (appears after touching the spinner)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = (Spinner)findViewById(spinnerRes);
        if(spinner != null)
            spinner.setAdapter(adapter);

        return spinner;
    }


    private void inflateYearButton()
    {
        // Button which opens a dialog to select the year of publication
        mYearButton = (Button) findViewById(R.id.year_button);

        if (mYearButton != null)
            mYearButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    final YearDialog yearDialog = new YearDialog(GenderActivity.this);
                    yearDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
                    {
                        @Override
                        public void onDismiss(DialogInterface dialog)
                        {
                            mYear = yearDialog.getYear();
                            // Set year button to the selected year
                            mYearButton.setText(String.valueOf(mYear));
                        }
                    });
                }
            });
    }


    private void inflateDetectButton()
    {
        Button detectButton = (Button)findViewById(R.id.detect_button);
        if(detectButton != null)
            detectButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    try
                    {
                        String title = mTitleField.getText().toString();
                        if("".equals(title))
                            throw new InvalidInputException(R.string.title_error);

                        if(mYear == 0)
                            throw new InvalidInputException(R.string.year_error);

                        String setting = getInputFromRadioGroup(mSettingGroup, R.string.setting_error);

                        String pov = getInputFromRadioGroup(mPovGroup, R.string.pov_error);

                        String detective = getInputFromSpinner(mDetectiveSpin, R.string.detective_error);

                        String cause = getInputFromSpinner(mCauseSpin, R.string.cause_error);

                        String victim = getInputFromSpinner(mVictimSpin, R.string.victim_error);

                        KillerDataset.clear();
                        KillerDataset killerDataset = KillerDataset.get(GenderActivity.this);
                        killerDataset.setData(title, mYear, setting, pov, detective, cause, victim);
                        showPrediction(killerDataset);

                    }
                    catch (InvalidInputException iie)
                    {
                        iie.printToast(GenderActivity.this);
                    }
                }
            });
    }


    private String getInputFromRadioGroup(RadioGroup group, int errorRes) throws InvalidInputException
    {
        // Getting checked point of view radio button, if any
        int selectedId = group.getCheckedRadioButtonId();

        RadioButton radioButton = (RadioButton) findViewById(selectedId);
        if (radioButton == null)
            throw new InvalidInputException(errorRes);
        else
            return radioButton.getText().toString();
    }


    private String getInputFromSpinner(Spinner spinner, int errorRes) throws InvalidInputException
    {
        View spinnerView = spinner.getSelectedView();

        if(R.string.select_label == (int)spinnerView.getId())
            throw new InvalidInputException(errorRes);
        else
            return spinner.toString();
    }


    private void showPrediction(KillerDataset killerDataset)
    {
        String label = killerDataset.classify();

        switch (label)
        {
            case "F": mPrediction = R.string.female; break;
            case "M": mPrediction = R.string.male; break;
            case "Lots": mPrediction = R.string.lots; break;
            case "unknown": mPrediction = R.string.unknown; break;
        }

        Intent intent = new Intent(GenderActivity.this, GenderPredictionActivity.class);
        intent.putExtra("dataset", mPrediction);

        startActivityForResult(intent, 0);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // If user confirmed login or registration process, retrieve the corresponding user object
        if(resultCode == RESULT_OK)
        {
            mTitleField.setText("");
            mYearButton.setText(R.string.select_label);
            mDetectiveSpin.setSelection(0);
            mSettingGroup.clearCheck();
            mPovGroup.clearCheck();
            mCauseSpin.setSelection(0);
            mVictimSpin.setSelection(0);
        }
    }
}
