package com.clemSP.iteration1.activities;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.clemSP.iteration1.R;
import com.clemSP.iteration1.model.AppAttribute;

import java.util.ArrayList;
import java.util.List;


/**
 * Dialog with checkboxes to select the features to be used for the prediction.
 */
public class FeaturesDialog extends BaseDialog
{
    private List<CheckBox> mFeaturesBoxes;
    private boolean[] mSelectedFeatures;


    public FeaturesDialog(Context context, boolean predictWeapon)
    {
        super(context, R.layout.dialog_features);

        mFeaturesBoxes = new ArrayList<>(AppAttribute.getNumAttributes());
        mSelectedFeatures = new boolean[AppAttribute.getNumAttributes()];

        inflateFeaturesCheckboxes(predictWeapon);
        inflateCancelButton(R.id.features_back_button);
        inflateFeaturesOkButton(context);

        show();
    }


    private void inflateFeaturesCheckboxes(boolean predictWeapon)
    {
        int index = -1;

        mFeaturesBoxes.add(++index, (CheckBox) findViewById(R.id.year_checkbox));
        mFeaturesBoxes.add(++index, (CheckBox) findViewById(R.id.detective_checkbox));
        mFeaturesBoxes.add(++index, (CheckBox) findViewById(R.id.setting_checkbox));
        mFeaturesBoxes.add(++index, (CheckBox) findViewById(R.id.pov_checkbox));

        mFeaturesBoxes.add(++index, (CheckBox) findViewById(R.id.weapon_checkbox));
        // Cannot have features for the two potential classes at the same time
        if(predictWeapon)
            mFeaturesBoxes.get(index).setVisibility(View.GONE);
        else
            mFeaturesBoxes.get(index).setVisibility(View.VISIBLE);

        mFeaturesBoxes.add(++index, (CheckBox) findViewById(R.id.victim_checkbox));

        mFeaturesBoxes.add(++index, (CheckBox) findViewById(R.id.murderer_checkbox));
        // Cannot have features for the two potential classes at the same time
        if(!predictWeapon)
            mFeaturesBoxes.get(index).setVisibility(View.GONE);
        else
            mFeaturesBoxes.get(index).setVisibility(View.VISIBLE);
    }


    private void inflateFeaturesOkButton(final Context context)
    {
        Button okButton = (Button) findViewById(R.id.features_ok_button);

        okButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int selectedCount = 0;

                for(int index = 0; index < mFeaturesBoxes.size(); index++)
                {
                    CheckBox box = mFeaturesBoxes.get(index);

                    if(box.isChecked())
                    {
                        selectedCount++;
                        mSelectedFeatures[index] = true;
                    }
                }

                if(selectedCount < 2)
                {
                    printErrorToast(context, R.string.feature_error);
                    return;
                }

                mCancelled = false;
                dismiss();
            }
        });
    }


    public boolean[] getSelectedFeatures()
    {
        return mSelectedFeatures;
    }
}
