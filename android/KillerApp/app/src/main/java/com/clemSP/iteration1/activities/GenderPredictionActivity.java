package com.clemSP.iteration1.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.clemSP.iteration1.R;
import com.clemSP.iteration1.model.VariableDataset;
import com.clemSP.iteration1.model.Wheel;


/**
 * Activity with an animated view flipper to display the predicted gender of the killer.
 */
public class GenderPredictionActivity extends BaseActivity
{
    private ViewFlipper mViewFlipper;
    private TextView mCaption, mConfidence;
    private Spinner mGenderSpinner;

    private Wheel mWheel;

    private int mWheelSpeed;
    private int mGenderRes, mGenderIndex;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gender_prediction);

        retrievePrediction();

        inflateWidgets();
        inflateButtons();
    }


    private void retrievePrediction()
    {
        Bundle bundle = getIntent().getExtras();
        // Get resource id of the string containing the predicted weapon
        mGenderRes = bundle.getInt("dataset");

        // Match the resource to the image in the layout ViewFlipper
        switch (mGenderRes)
        {
            case R.string.female: mGenderIndex = 0; break;
            case R.string.lots: mGenderIndex = 1; break;
            case R.string.male: mGenderIndex = 2; break;
            case R.string.none: mGenderIndex = 3; break;
            default:
                mGenderRes = R.string.error;
                mGenderIndex = 4; break;
        }
    }


    private void inflateWidgets()
    {
        mViewFlipper = (ViewFlipper)findViewById(R.id.view_flipper);
        mWheel = new Wheel(mViewFlipper);

        mCaption = (TextView)findViewById(R.id.weapon_caption);

        mConfidence = (TextView)findViewById(R.id.confidence_textview);

        Button backButton = (Button) findViewById(R.id.back_button);
        if(backButton == null)
            return;

        backButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setResult(RESULT_CANCELED);
                GenderPredictionActivity.this.finish();
            }
        });

        mWheelSpeed = 500;

        // Start thread to animate the ViewFlipper
        Handler h = new Handler();
        h.postDelayed(r1, mWheelSpeed);
    }


    private Runnable r1 = new Runnable()
    {
        @Override
        public void run()
        {
            mViewFlipper.startFlipping();
            mWheel.animateUp();

            // Stop wheel on predicted weapon
            if(mViewFlipper.getDisplayedChild() == mGenderIndex)
            {
                mViewFlipper.stopFlipping();
                String weaponString = getResources().getString(mGenderRes);
                mCaption.setText(weaponString);
                mConfidence.setText(getConfidence());
                inflateButtons();
            }
            // Keep the wheel turning
            else
            {
                Handler h = new Handler();
                h.postDelayed(r1, mWheelSpeed);
            }
        }

    };


    private int getConfidence()
    {
        int percentage = VariableDataset.get(this, false).getConfidence();

        if(percentage < 1 || percentage > 100)
            return R.string.error;
        if(percentage < 33)
            return R.string.wild_guess;
        else if(percentage < 67)
            return R.string.quite_confident;
        else
            return R.string.very_confident;
    }


    private void inflateButtons()
    {
        Button correctButton = (Button) findViewById(R.id.correct_button);
        if(correctButton == null)
            return;

        correctButton.setVisibility(View.VISIBLE);

        correctButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setResult(RESULT_OK);
                GenderPredictionActivity.this.finish();
            }
        });

        Button incorrectButton = (Button) findViewById(R.id.incorrect_button);
        if(incorrectButton == null)
            return;

        incorrectButton.setVisibility(View.VISIBLE);

        incorrectButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                retrainDialog();
            }
        });
    }


    private void retrainDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.retrain_question);
        builder.setCancelable(false);
        builder.setNegativeButton(R.string.negative_button, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                setResult(RESULT_OK);
                GenderPredictionActivity.this.finish();
            }
        });

        builder.setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                rightAnswerDialog();
            }
        });

        Dialog yearDialog = builder.create();
        yearDialog.show();
    }

    private void rightAnswerDialog()
    {
        Dialog answerDialog = new Dialog(GenderPredictionActivity.this);

        answerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Setting custom layout of the dialog
        answerDialog.setContentView(R.layout.dialog_right_answer);
        // Pressing the back button won't close the dialog
        answerDialog.setCancelable(false);

        inflateDialogSpinner(answerDialog);
        inflateDialogButtons(answerDialog);

        answerDialog.show();
    }


    private void inflateDialogSpinner(Dialog dialog)
    {
        // String array containing the different detectives
        String[] genders = getResources().getStringArray(R.array.gender_array);

        // Adding detective entries and custom layout to spinner
        ArrayAdapter<String> genders_adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, genders);
        // Setting layout of the drop down menu (appears after touching the spinner)
        genders_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mGenderSpinner = (Spinner) dialog.findViewById(R.id.spinner);
        if(mGenderSpinner == null)
            return;
        mGenderSpinner.setAdapter(genders_adapter);
    }

    private void inflateDialogButtons(final Dialog dialog)
    {
        Button cancelButton = (Button) dialog.findViewById(R.id.cancel_right_answer);
        if(cancelButton == null)
            return;

        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });

        Button okButton = (Button) dialog.findViewById(R.id.confirm_right_answer);
        if(okButton == null)
            return;

        okButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                View weapon = mGenderSpinner.getSelectedView();
                if(R.string.select_label == (int)weapon.getId())
                {
                    printErrorToast(R.string.right_answer_error);
                    return;
                }
                VariableDataset.get(GenderPredictionActivity.this, false).retrainClassifier();
                dialog.dismiss();
                setResult(RESULT_OK);
                GenderPredictionActivity.this.finish();
            }
        });
    }
}
