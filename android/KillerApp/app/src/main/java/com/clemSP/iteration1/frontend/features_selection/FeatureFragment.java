package com.clemSP.iteration1.frontend.features_selection;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.clemSP.iteration1.R;
import com.clemSP.iteration1.backend.AppAttribute;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class FeatureFragment extends DialogFragment
{
    public static final String TAG = "featuresFragment";

    private SelectorListener mListener;

    private List<CheckBox> mFeaturesBoxes;
    private boolean[] mSelectedFeatures;

    private int mSelectedCount;


    public interface SelectorListener extends Serializable
    {
        void onOkButtonPressed(FeatureFragment selector);
    }


    public FeatureFragment() { }


    public static FeatureFragment newInstance(boolean predictWeapon, boolean[] selectedFeatures,
                                              SelectorListener listener)
    {
        FeatureFragment selector = new FeatureFragment();

        Bundle bundle = new Bundle();
        bundle.putBoolean("predictWeapon", predictWeapon);
        bundle.putBooleanArray("selectedFeatures", selectedFeatures);
        bundle.putSerializable("listener", listener);

        selector.setArguments(bundle);

        return selector;
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mFeaturesBoxes = new ArrayList<>(AppAttribute.getNumAttributes());
        mSelectedFeatures = getArguments().getBooleanArray("selectedFeatures");
        mListener = (SelectorListener) getArguments().getSerializable("listener");
    }


    @Override @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Dialog dialog = new Dialog(getActivity());

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_features);

        // Pressing the back button won't close the dialog
        dialog.setCancelable(false);

        inflateFeaturesCheckboxes(dialog, getArguments().getBoolean("predictWeapon"));
        inflateSelectButtons(dialog);
        inflateCancelButton(dialog);
        inflateFeaturesOkButton(dialog);

        return dialog;
    }


    private void inflateFeaturesCheckboxes(Dialog dialog, boolean predictWeapon)
    {
        for(int index = 0; index < mSelectedFeatures.length; index++)
        {
            CheckBox box = (CheckBox) dialog.findViewById(AppAttribute.getCheckboxRes(index));
            if((predictWeapon && index == AppAttribute.Weapon.getIndex()) ||
                    (!predictWeapon && index == AppAttribute.Murderer.getIndex()))
                box.setVisibility(View.GONE);
            else
                box.setChecked(mSelectedFeatures[index]);
            mFeaturesBoxes.add(box);
        }
    }


    private void inflateSelectButtons(Dialog dialog)
    {
        Button selectallButton = (Button) dialog.findViewById(R.id.features_selectall);

        selectallButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                for(CheckBox box : mFeaturesBoxes)
                    box.setChecked(box.getVisibility() == View.VISIBLE);
            }
        });

        Button deselectallButton = (Button) dialog.findViewById(R.id.features_deselectall);

        deselectallButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                for(CheckBox box : mFeaturesBoxes)
                    box.setChecked(false);
            }
        });
    }


    private void inflateCancelButton(final Dialog dialog)
    {
        Button cancelButton = (Button) dialog.findViewById(R.id.features_back_button);
        if(cancelButton == null)
            return;

        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dismiss();
            }
        });
    }

    private void inflateFeaturesOkButton(final Dialog dialog)
    {
        Button okButton = (Button) dialog.findViewById(R.id.features_ok_button);

        okButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mSelectedCount = 0;

                for(int index = 0; index < mFeaturesBoxes.size(); index++)
                {
                    CheckBox box = mFeaturesBoxes.get(index);

                    if(box.isChecked())
                    {
                        mSelectedCount++;
                        mSelectedFeatures[index] = true;
                    }
                    else
                        mSelectedFeatures[index] = false;
                }

                if(mSelectedCount < 2)
                {
                    Toast.makeText(dialog.getContext(), R.string.feature_error, Toast.LENGTH_SHORT).show();
                    return;
                }
                mListener.onOkButtonPressed(FeatureFragment.this);
                dismiss();
            }
        });
    }


    public boolean[] getSelectedFeatures()
    {
        return mSelectedFeatures;
    }
}

