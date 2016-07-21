package com.clemSP.iteration1.activities;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.clemSP.iteration1.R;


/**
 * Dialog with radio buttons to select the type of prediction needed.
 */
public class ClassDialog extends BaseDialog
{
    private RadioGroup mPredictionGroup;
    private boolean mPredictWeapon;


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
                int selectedId = mPredictionGroup.getCheckedRadioButtonId();

                if(selectedId == R.id.weapon_button)
                    mPredictWeapon = true;

                else if(selectedId == R.id.gender_button)
                    mPredictWeapon = false;

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


    public boolean getPredictWeapon()
    {
        return mPredictWeapon;
    }
}
