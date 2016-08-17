package com.clemSP.iteration1.frontend.prediction;

import com.clemSP.iteration1.R;
import com.clemSP.iteration1.backend.Data;
import com.clemSP.iteration1.backend.Dataset;
import com.clemSP.iteration1.frontend.BaseActivity;
import com.clemSP.iteration1.frontend.ImageFeature;
import com.clemSP.iteration1.frontend.dataset_management.DatasetTask;
import com.clemSP.iteration1.frontend.dataset_management.UpdateLocalDatasetTask;
import com.clemSP.iteration1.frontend.dataset_management.UpdateServerTask;

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


/** Class which displays the prediction as well as two buttons to (in)validate the prediction. */
public abstract class PredictionActivity extends BaseActivity implements DatasetTask.DatasetTaskListener
{
    private static final String URL = "https://murder-mystery-server.herokuapp.com/killerapp/update/";

    protected ImageFeature mPrediction;

    protected Spinner mRetrainSpinner;

    private Data mNewInstance;


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
                finishThisActivity(RESULT_OK);
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
            	showRetrainDialog();
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
                finishThisActivity(RESULT_CANCELED);
            }
        });
    }


    private AlertDialog.Builder getDialogBuilder(int messageRes)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(messageRes);
        builder.setCancelable(false);

        return builder;
    }


    /** Shows a pop-up dialog to ask the user if they want to input the correct answer
      * after a wrong prediction.
      */
    private void showRetrainDialog()
    {
        AlertDialog.Builder builder = getDialogBuilder(R.string.retrain_question);

        builder.setNegativeButton(R.string.negative_button, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                finishThisActivity(RESULT_OK);
            }
        });

        builder.setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                showRightAnswerDialog();
            }
        });

        builder.create().show();
    }


    /** Shows a pop-up dialog with a Spinner for the user to input the correct answer. */
    private void showRightAnswerDialog()
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


    /** Sets up the spinner in the dialog for the right answer. */
    private void inflateDialogSpinner(Dialog dialog)
    {
        // String array containing the different entries
        String[] entries = getResources().getStringArray(getSpinnerEntries());

        // Adding entries and custom layout to spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, entries);

        // Setting layout of the drop down menu (appears after touching the spinner)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mRetrainSpinner = (Spinner) dialog.findViewById(R.id.spinner);
        if(mRetrainSpinner == null)
            return;
        mRetrainSpinner.setAdapter(adapter);
    }


    protected abstract int getSpinnerEntries();


    /** Sets up the buttons in the dialog for the right answer. */
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
                String rightAnswer = mRetrainSpinner.getSelectedItem().toString();
                if(getString(R.string.select_label).equals(rightAnswer))
                {
                    printErrorToast(R.string.right_answer_error);
                    return;
                }
                Dataset.get(PredictionActivity.this).retrainClassifier(PredictionActivity.this,
                		rightAnswer);
                dialog.dismiss();

                mNewInstance = Dataset.get(PredictionActivity.this).getLabelledData();
                new UpdateLocalDatasetTask(PredictionActivity.this, null, mNewInstance).execute();
            }
        });
    }


    @Override
    public void onTaskCompleted(DatasetTask task, int status)
    {
        if (task instanceof UpdateLocalDatasetTask)
        {
            if(status == DatasetTask.POSITIVE_RESULT)
                showServerDialog();
            else
                showUpdateLocalErrorDialog();
        }
        else if (task instanceof UpdateServerTask)
        {
            // status is true if the local dataset is different from the server's
            if (status == DatasetTask.POSITIVE_RESULT)
                finishThisActivity(RESULT_OK);
            else
                showUpdateServerErrorDialog();
        }
    }


    /** Shows a pop-up dialog to ask the user if they want to update the server
     * with the correct answer they just input. */
    private void showServerDialog()
    {
        AlertDialog.Builder builder = getDialogBuilder(R.string.send_data_question);

        builder.setNegativeButton(R.string.negative_button, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                finishThisActivity(RESULT_OK);
            }
        });

        builder.setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                //final String URL = "https://murder-mystery-server.herokuapp.com/killerapp/update";
                new UpdateServerTask(PredictionActivity.this, URL, mNewInstance).execute();
            }
        });

        builder.create().show();
    }


    /** Shows an error dialog in case the app failed to update the local dataset. */
    private void showUpdateLocalErrorDialog()
    {
        AlertDialog.Builder builder = getDialogBuilder(R.string.local_update_error);

        builder.setNegativeButton(R.string.negative_button, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                showServerDialog();
            }
        });

        builder.setPositiveButton(R.string.try_again_label, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                new UpdateLocalDatasetTask(PredictionActivity.this, null, mNewInstance).execute();
            }
        });

        builder.create().show();
    }


    /** Shows an error dialog in case the app failed to update the server. */
    private void showUpdateServerErrorDialog()
    {
        AlertDialog.Builder builder = getDialogBuilder(R.string.server_update_error);

        builder.setNegativeButton(R.string.negative_button, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                finishThisActivity(RESULT_OK);
            }
        });

        builder.setPositiveButton(R.string.try_again_label, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                new UpdateServerTask(PredictionActivity.this, URL, mNewInstance).execute();
            }
        });

        builder.create().show();
    }


    private void finishThisActivity(int result)
    {
        setResult(result);
        PredictionActivity.this.finish();
    }
}
