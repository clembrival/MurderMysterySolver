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
import android.widget.ViewFlipper;

import com.clemSP.iteration1.R;
import com.clemSP.iteration1.model.KillerClassifier;
import com.clemSP.iteration1.model.Prediction;

/**
 * Based on tutorial by William J. Francis
 * http://www.techrepublic.com/blog/software-engineer/building-a-slot-machine-in-android-viewflipper-meet-gesture-detector/
 */
public class PredictionActivity extends AppCompatActivity
{
    private ViewFlipper mViewFlipper;
    private TextView mCaption;
    private Spinner mWeaponSpinner;

    private int mWheelSpeed;
    private int mWeaponRes, mWeaponIndex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction);

        retrievePrediction();

        inflateWidgets();
    }


    private void retrievePrediction()
    {
        Bundle bundle = getIntent().getExtras();
        // Get resource id of the string containing the predicted weapon
        mWeaponRes = bundle.getInt("prediction");

        // Match the resource to the image in the layout ViewFlipper
        switch (mWeaponRes)
        {
            case R.string.accident: mWeaponIndex = 0; break;
            case R.string.concussion: mWeaponIndex = 1; break;
            case R.string.drowning: mWeaponIndex = 2; break;
            case R.string.poison: mWeaponIndex = 3; break;
            case R.string.shooting: mWeaponIndex = 4; break;
            case R.string.stabbing: mWeaponIndex = 5; break;
            case R.string.throatslit: mWeaponIndex = 6; break;
            case R.string.unknown: mWeaponIndex = 7; break;
            default:
                mWeaponRes = R.string.weapon_error;
                mWeaponIndex = 7; break;
        }
    }

    private void inflateWidgets()
    {
        mViewFlipper = (ViewFlipper)findViewById(R.id.view_flipper);

        mCaption = (TextView)findViewById(R.id.weapon_caption);

        Button backButton = (Button) findViewById(R.id.back_button);
        if(backButton == null)
            return;

        backButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setResult(RESULT_CANCELED);
                PredictionActivity.this.finish();
            }
        });

        // Duration of the images animation
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
            animate();

            // Stop wheel on predicted weapon
            if(mViewFlipper.getDisplayedChild() == mWeaponIndex)
            {
                mViewFlipper.stopFlipping();
                String weaponString = getResources().getString(mWeaponRes);
                mCaption.setText(weaponString);
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

    private void animate()
    {
        // Setup animation
        Animation inFromBottom = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromBottom.setInterpolator(new AccelerateInterpolator());
        inFromBottom.setDuration(mWheelSpeed);

        Animation outToTop = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -1.0f);
        outToTop.setInterpolator(new AccelerateInterpolator());
        outToTop.setDuration(mWheelSpeed);

        mViewFlipper.setInAnimation(inFromBottom);
        mViewFlipper.setOutAnimation(outToTop);

        if (mViewFlipper.getDisplayedChild()==0)
            mViewFlipper.setDisplayedChild(7);
        else
            mViewFlipper.showPrevious();
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
                PredictionActivity.this.finish();
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
                PredictionActivity.this.finish();
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
        Dialog answerDialog = new Dialog(PredictionActivity.this);

        answerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Setting custom layout of the dialog
        answerDialog.setContentView(R.layout.right_answer_dialog);
        // Pressing the back button won't close the dialog
        answerDialog.setCancelable(false);

        inflateDialogSpinner(answerDialog);
        inflateDialogButtons(answerDialog);

        answerDialog.show();
    }


    private void inflateDialogSpinner(Dialog dialog)
    {
        // String array containing the different detectives
        String[] weapons = getResources().getStringArray(R.array.cause_array);

        // Adding detective entries and custom layout to spinner
        ArrayAdapter<String> weapons_adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, weapons);
        // Setting layout of the drop down menu (appears after touching the spinner)
        weapons_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mWeaponSpinner = (Spinner) dialog.findViewById(R.id.weapon_spinner);
        if(mWeaponSpinner == null)
            return;
        mWeaponSpinner.setAdapter(weapons_adapter);
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
                //retrainClassifier();
                dialog.dismiss();
                setResult(RESULT_OK);
                PredictionActivity.this.finish();
            }
        });
    }


    private void retrainClassifier()
    {
        KillerClassifier.get(this).retrain(Prediction.get(this).getLastInstance());
    }

}
