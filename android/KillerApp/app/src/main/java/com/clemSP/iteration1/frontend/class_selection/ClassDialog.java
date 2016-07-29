package com.clemSP.iteration1.frontend.class_selection;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import com.clemSP.iteration1.R;
import com.clemSP.iteration1.frontend.BaseDialog;
import com.clemSP.iteration1.frontend.PredictionSettings;


/**
 * Dialog with radio buttons to select the type of prediction needed.
 */
public class ClassDialog extends BaseDialog
{
    private RadioGroup mPredictionGroup;


    public ClassDialog(Context context)
    {
        super(context, R.layout.dialog_select_prediction);

        mPredictionGroup = (RadioGroup) findViewById(R.id.prediction_group);

        inflateCancelButton(R.id.prediction_cancel_button);
        inflateOkButton(context);

        show();
    }


    private void inflateOkButton(final Context context)
    {
        Button okButton = (Button) findViewById(R.id.prediction_ok_button);

        okButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                PredictionSettings settings = PredictionSettings.getSettings();
                int selectedId = mPredictionGroup.getCheckedRadioButtonId();

                if(selectedId == R.id.weapon_button)
                    settings.setPredictWeapon(true);

                else if(selectedId == R.id.gender_button)
                    settings.setPredictWeapon(false);

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
