package com.clemSP.iteration1.frontend.features_input;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.clemSP.iteration1.R;
import com.clemSP.iteration1.backend.Dataset;
import com.clemSP.iteration1.frontend.InvalidInputException;
import com.clemSP.iteration1.backend.AttributeFactory.AppAttribute;
import com.clemSP.iteration1.backend.Data;


public class TextInputFragment extends BaseInputFragment
{
    private Spinner mDetectiveSpin, mWeaponSpin, mGenderSpin, mVictimSpin;
    private RadioGroup mSettingGroup;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        mView = inflater.inflate(R.layout.fragment_text_input, container, false);

        inflateWidgets(true);
        super.inflateYearButton(true);
        inflateDetectButton();

        retrieveSavedWidgets(savedInstanceState);

        return mView;
    }


    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        if(mDetectiveSpin != null)
            outState.putInt("detective", mDetectiveSpin.getSelectedItemPosition());

        if(mWeaponSpin != null)
            outState.putInt("weapon", mWeaponSpin.getSelectedItemPosition());

        if(mGenderSpin != null)
            outState.putInt("gender", mGenderSpin.getSelectedItemPosition());

        if(mVictimSpin != null)
            outState.putInt("victim", mVictimSpin.getSelectedItemPosition());

        if(mSettingGroup != null)
            outState.putInt("setting", mSettingGroup.getCheckedRadioButtonId());
    }


    @Override
    public void retrieveSavedWidgets(Bundle savedInstanceState)
    {
        super.retrieveSavedWidgets(savedInstanceState);

        int detective = retrieveSavedInt(savedInstanceState, "detective");
        if(mDetectiveSpin != null && detective != -1)
            mDetectiveSpin.setSelection(detective);

        int weapon = retrieveSavedInt(savedInstanceState, "weapon");
        if(mWeaponSpin != null && weapon != -1)
            mWeaponSpin.setSelection(weapon);

        int gender = retrieveSavedInt(savedInstanceState, "gender");
        if(mGenderSpin != null && gender != -1)
            mGenderSpin.setSelection(gender);

        int victim = retrieveSavedInt(savedInstanceState, "victim");
        if(mVictimSpin != null && victim != -1)
            mVictimSpin.setSelection(victim);

        int setting = retrieveSavedInt(savedInstanceState, "setting");
        if(mSettingGroup != null && setting != -1)
            mSettingGroup.check(setting);
    }


    @Override
    public void inflateWidgets(boolean clear)
    {
        super.inflateWidgets(clear);

        mSettingGroup = inflateRadioGroup(AppAttribute.Location, R.id.setting_layout,
                R.id.setting_group, clear);

        mDetectiveSpin = inflateSpinner(AppAttribute.Detective, R.id.detective_layout,
                R.id.detective_spinner, R.array.detective_array, clear);

        if(mSettings.getPredictWeapon())
        {
            mWeaponSpin = inflateSpinner(AppAttribute.Weapon, R.id.weapon_layout,
                    R.id.weapon_spinner, R.array.cause_array, true);

            mGenderSpin = inflateSpinner(AppAttribute.Murderer, R.id.weapon_layout,
                    R.id.weapon_spinner, R.array.gender_array, clear);
            if(mGenderSpin != null)
                inflateLabel(R.id.weapon_textview, R.string.murderer_label);
        }
        else
        {
            mGenderSpin = inflateSpinner(AppAttribute.Murderer, R.id.weapon_layout,
                    R.id.weapon_spinner, R.array.gender_array, true);

            mWeaponSpin = inflateSpinner(AppAttribute.Weapon, R.id.weapon_layout,
                    R.id.weapon_spinner, R.array.cause_array, clear);
            if(mWeaponSpin != null)
                inflateLabel(R.id.weapon_textview, R.string.cause_label);
        }

        mVictimSpin = inflateSpinner(AppAttribute.Victim, R.id.victim_layout,
                R.id.victim_spinner, R.array.gender_array, clear);
    }


    private RadioGroup inflateRadioGroup(AppAttribute attribute, int layoutRes, int groupRes,
                                         boolean clear)
    {
        RelativeLayout layout = (RelativeLayout) mView.findViewById(layoutRes);

        if(layout != null)
        {
            if (!mSettings.getFeatureIsSelected(attribute.getIndex()))
                layout.setVisibility(View.GONE);
            else
            {
                layout.setVisibility(View.VISIBLE);

                RadioGroup radioGroup = (RadioGroup) mView.findViewById(groupRes);
                if(clear && radioGroup != null)
                    radioGroup.clearCheck();

                return radioGroup;
            }
        }

        return null;
    }


    private Spinner inflateSpinner(AppAttribute attribute, int layoutRes, int spinnerRes,
                                   int entriesRes, boolean clear)
    {
        RelativeLayout layout = (RelativeLayout) mView.findViewById(layoutRes);

        // String array containing the different entries
        String[] entries = getResources().getStringArray(entriesRes);

        // Adding entries and custom layout to spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mView.getContext(),
                R.layout.spinner_layout, entries);

        // Setting layout of the drop down menu (appears after touching the spinner)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = (Spinner) mView.findViewById(spinnerRes);
        if (spinner != null)
        {
            spinner.setAdapter(adapter);
            if(clear)
                spinner.setSelection(0);
        }

        if(layout != null)
            if (!mSettings.getFeatureIsSelected(attribute.getIndex()))
            {
                layout.setVisibility(View.GONE);
                if(spinner != null)
                    spinner.setSelection(0);
            }
            else
                layout.setVisibility(View.VISIBLE);

        return spinner;
    }


    private void inflateLabel(int textviewRes, int labelRes)
    {
        TextView label = (TextView) mView.findViewById(textviewRes);
        if(label != null)
            label.setText(labelRes);
    }


    private void inflateDetectButton()
    {
        Button detectButton = (Button) mView.findViewById(R.id.detect_button);
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

                        data.setPov(getInputFromRadioGroup(AppAttribute.Pov, mPovGroup,
                                R.string.pov_error));

                        data.setDetective(getInputFromSpinner(AppAttribute.Detective,
                                mDetectiveSpin, R.string.detective_error));

                        String victim = getInputFromSpinner(AppAttribute.Victim, mVictimSpin,
                                R.string.victim_error);
                        if(getString(R.string.female).equals(victim))
                            data.setVictim("F");
                        else if(getString(R.string.male).equals(victim))
                            data.setVictim("M");
                        else
                            data.setVictim(victim);

                        data.setWeapon(getInputFromSpinner(AppAttribute.Weapon, mWeaponSpin,
                                R.string.cause_error));

                        String gender = getInputFromSpinner(AppAttribute.Murderer, mGenderSpin,
                                R.string.gender_error);
                        if(getString(R.string.female).equals(gender))
                            data.setGender("F");
                        else if(getString(R.string.male).equals(gender))
                            data.setGender("M");
                        else
                            data.setGender(victim);

                        data.setSetting(getInputFromRadioGroup(AppAttribute.Location,
                                mSettingGroup, R.string.setting_error));

                        data.setRating("" + getInputRating());

                        data.setOthers();

                        Log.w("INPUT", data.toString());
                        Dataset.clear();
                        Dataset dataset = Dataset.get(getActivity());
                        dataset.setData(data);
                        mListener.onFeaturesInput(dataset.classify());
                    }
                    catch (InvalidInputException iie)
                    {
                        iie.printToast(mView.getContext());
                    }
                }
            });
    }

    @Override
    public void update(boolean clear)
    {
    	inflateWidgets(clear);
        super.inflateYearButton(clear);
    }
}
