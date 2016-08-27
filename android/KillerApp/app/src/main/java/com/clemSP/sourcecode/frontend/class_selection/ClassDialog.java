package com.clemSP.sourcecode.frontend.class_selection;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.clemSP.sourcecode.R;
import com.clemSP.sourcecode.frontend.BaseDialog;
import com.clemSP.sourcecode.frontend.PredictionSettings;


/**
 * Dialog with radio buttons to select the type of prediction needed.
 */
public class ClassDialog extends BaseDialog
{
    /** The RadioGroup containing Radio Buttons for each one of the attributes which can be predicted. */
    private RadioGroup mPredictionGroup;


    /**
     * @param context the Context in which the dialog should appear.
     */
    public ClassDialog(Context context)
    {
        super(context, R.layout.dialog_select_prediction);

        mPredictionGroup = (RadioGroup) findViewById(R.id.prediction_group);

        preSelectButton();
        super.inflateCancelButton(R.id.prediction_cancel_button);
        inflateOkButton(context);

        show();
    }


    /**
     * Checks the radio button corresponding to the pre-selected attribute to be predicted.
     */
    private void preSelectButton()
    {
        PredictionSettings settings = PredictionSettings.getSettings();

        int checkedButtonId = settings.getPredictWeapon() ? R.id.weapon_button : R.id.gender_button;

        ((RadioButton) findViewById(checkedButtonId)).setChecked(true);
    }


    /**
     * Inflates the OK button and adds a ClickListener to it.
     * @param context the Context in which the dialog should appear.
     */
    private void inflateOkButton(final Context context)
    {
        Button okButton = (Button) findViewById(R.id.prediction_ok_button);

        okButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Using Adapter design pattern because ClassDialog already has a superclass
                ClassSelector.Adaptee classSelector = new ClassSelector.Adaptee();

                // Get the id of the radio button checked, if any
                int selectedId = mPredictionGroup.getCheckedRadioButtonId();

                if(selectedId == R.id.weapon_button)
                    classSelector.updatePredictionTarget(true);

                else if(selectedId == R.id.gender_button)
                    classSelector.updatePredictionTarget(false);

                // The user pressed OK before any attribute was selected
                else
                {
                    printErrorToast(context, R.string.prediction_error);
                    return;
                }

                mCancelled = false;
                dismiss();
            }
        });
    }
}
