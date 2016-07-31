package com.clemSP.iteration1.frontend.prediction;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.clemSP.iteration1.R;
import com.clemSP.iteration1.backend.Dataset;
import com.clemSP.iteration1.frontend.BaseActivity;
import com.clemSP.iteration1.frontend.ImageFeature;


public abstract class PredictionActivity extends BaseActivity
{
    protected ImageFeature mPrediction;

    protected Spinner mRetrainSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction);

        retrievePrediction();

        inflateWidgets();
        inflateButtons();
    }


    private void retrievePrediction()
    {
        Bundle bundle = getIntent().getExtras();
        // Get resource id of the string containing the predicted weapon
        int predictionRes = bundle.getInt("dataset");

        setImageRes(predictionRes);
    }


    protected abstract void setImageRes(int predictionRes);


    private void inflateWidgets()
    {
        ImageView imageView = (ImageView) findViewById(R.id.image_view);
        if(imageView == null)
            return;

        imageView.setImageResource(mPrediction.getImageRes());
        imageView.setContentDescription(getString(mPrediction.getCaptionRes()));

        TextView caption = (TextView) findViewById(R.id.image_caption);
        if(caption == null)
            return;

        caption.setText(mPrediction.getCaptionRes());

        TextView confidence = (TextView) findViewById(R.id.confidence_textview);
        if(confidence == null)
            return;

        confidence.setText(getConfidence());

        inflateButtons();
    }


    private int getConfidence()
    {
        int percentage = Dataset.get(this).getConfidence();

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

        incorrectButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                retrainDialog();
            }
        });

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
        answerDialog.setContentView(R.layout.dialog_right_answer);
        // Pressing the back button won't close the dialog
        answerDialog.setCancelable(false);

        inflateDialogSpinner(answerDialog);
        inflateDialogButtons(answerDialog);

        answerDialog.show();
    }


    private void inflateDialogSpinner(Dialog dialog)
    {
        // String array containing the different entries
        String[] weapons = getResources().getStringArray(getSpinnerEntries());

        // Adding entries and custom layout to spinner
        ArrayAdapter<String> weapons_adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, weapons);

        // Setting layout of the drop down menu (appears after touching the spinner)
        weapons_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mRetrainSpinner = (Spinner) dialog.findViewById(R.id.spinner);
        if(mRetrainSpinner == null)
            return;
        mRetrainSpinner.setAdapter(weapons_adapter);
    }


    protected abstract int getSpinnerEntries();


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
                View weapon = mRetrainSpinner.getSelectedView();
                if(R.string.select_label == (int)weapon.getId())
                {
                    printErrorToast(R.string.right_answer_error);
                    return;
                }
                Dataset.get(PredictionActivity.this).retrainClassifier();
                dialog.dismiss();
                setResult(RESULT_OK);
                PredictionActivity.this.finish();
            }
        });
    }
}
