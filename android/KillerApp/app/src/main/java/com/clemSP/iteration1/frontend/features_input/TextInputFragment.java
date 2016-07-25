package com.clemSP.iteration1.frontend.features_input;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.clemSP.iteration1.frontend.InvalidInputException;
import com.clemSP.iteration1.R;
import com.clemSP.iteration1.backend.AppAttribute;
import com.clemSP.iteration1.backend.Data;
import com.clemSP.iteration1.backend.VariableDataset;


public class TextInputFragment extends BaseInputFragment
{
    private Spinner mDetectiveSpin, mWeaponSpin, mGenderSpin, mVictimSpin;
    private RadioGroup mSettingGroup;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_text_input, container, false);

        inflateWidgets(view);
        super.inflateYearButton(view);
        inflateDetectButton(view);

        return view;
    }


    @Override
    public void inflateWidgets(View view)
    {
        super.inflateWidgets(view);

        mSettingGroup = inflateRadioGroup(view, AppAttribute.Location, R.id.setting_layout,
                R.id.setting_group);

        mDetectiveSpin = inflateSpinner(view, AppAttribute.Detective, R.id.detective_layout,
                R.id.detective_spinner, R.array.detective_array);

        if(mPredictWeapon)
            mGenderSpin = inflateSpinner(view, AppAttribute.Murderer, R.id.weapon_layout,
                    R.id.weapon_spinner, R.array.gender_array);
        else
            mWeaponSpin = inflateSpinner(view, AppAttribute.Weapon, R.id.weapon_layout,
                    R.id.weapon_spinner, R.array.cause_array);

        if(mGenderSpin != null || mWeaponSpin != null)
        {
            TextView label = (TextView) view.findViewById(R.id.weapon_textview);
            if(label != null)
                label.setText((mGenderSpin != null) ? R.string.murderer_label : R.string.cause_label);
        }

        mVictimSpin = inflateSpinner(view, AppAttribute.Victim, R.id.victim_layout,
                R.id.victim_spinner, R.array.gender_array);
    }


    private RadioGroup inflateRadioGroup(View view, AppAttribute attribute, int layoutRes, int groupRes)
    {
        RelativeLayout layout = (RelativeLayout) view.findViewById(layoutRes);

        if(layout != null)
        {
            if (!mSelectedFeatures[attribute.getIndex()])
                layout.setVisibility(View.GONE);
            else
                return (RadioGroup) view.findViewById(groupRes);
        }

        return null;
    }


    private Spinner inflateSpinner(View view, AppAttribute attribute, int layoutRes, int spinnerRes,
                                   int entriesRes)
    {
        RelativeLayout layout = (RelativeLayout) view.findViewById(layoutRes);

        if(layout != null)
            if (!mSelectedFeatures[attribute.getIndex()])
                layout.setVisibility(View.GONE);
            else
            {
                // String array containing the different entries
                String[] entries = getResources().getStringArray(entriesRes);

                // Adding entries and custom layout to spinner
                ArrayAdapter<String> adapter = new ArrayAdapter<>(view.getContext(),
                        R.layout.spinner_layout, entries);

                // Setting layout of the drop down menu (appears after touching the spinner)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                Spinner spinner = (Spinner) view.findViewById(spinnerRes);
                if (spinner != null)
                    spinner.setAdapter(adapter);
                return spinner;
            }
        return null;
    }


    private void inflateDetectButton(final View view)
    {
        Button detectButton = (Button) view.findViewById(R.id.detect_button);
        if(detectButton != null)
            detectButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    try
                    {
                        Data data = new Data();

                        data.setTitle(mTitleField.getText().toString());
                        if("".equals(data.getTitle()))
                            throw new InvalidInputException(R.string.title_error);

                        data.setYear("" + getInputYear());

                        data.setPov(getInputFromRadioGroup(view, AppAttribute.Pov, mPovGroup,
                                R.string.pov_error));

                        data.setDetective(getInputFromSpinner(AppAttribute.Detective,
                                mDetectiveSpin, R.string.detective_error));

                        data.setVictim(getInputFromSpinner(AppAttribute.Victim, mVictimSpin,
                                R.string.victim_error));

                        data.setWeapon(getInputFromSpinner(AppAttribute.Weapon, mWeaponSpin,
                                R.string.cause_error));

                        data.setGender(getInputFromSpinner(AppAttribute.Murderer, mGenderSpin,
                                R.string.gender_error));

                        data.setSetting(getInputFromRadioGroup(view, AppAttribute.Location,
                                mSettingGroup, R.string.setting_error));

                        VariableDataset.clear();
                        VariableDataset dataset = VariableDataset.get(view.getContext(), mPredictWeapon);
                        dataset.setData(data);
                        mCallback.onFeaturesInput(dataset.classify());
                    }
                    catch (InvalidInputException iie)
                    {
                        printErrorToast(view.getContext(), iie.getMessage());
                    }
                }
            });
    }

    @Override
    public void update()
    {
        //TODO
    }
}
