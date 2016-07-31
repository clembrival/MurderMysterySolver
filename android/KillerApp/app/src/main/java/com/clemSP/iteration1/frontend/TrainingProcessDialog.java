package com.clemSP.iteration1.frontend;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.clemSP.iteration1.R;
import com.clemSP.iteration1.backend.AppClassifier;


public class TrainingProcessDialog extends AsyncTask<Void, Void, Boolean>
{
    private ProgressDialog mProgressDialog;
    private Context mContext;
    private TrainingListener mListener;


    public interface TrainingListener
    {
        void onTrainingOver(boolean completed);
    }


    public TrainingProcessDialog(Activity activity)
    {
        mContext = activity;

        if(activity instanceof TrainingListener)
            mListener = (TrainingListener) activity;

    }

    @Override
    protected void onPreExecute()
    {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setTitle(mContext.getString(R.string.training_dialog_title));
        mProgressDialog.setMessage(mContext.getString(R.string.training_dialog_message));
        mProgressDialog.setCancelable(false);

        mProgressDialog.show();
    }


    @Override
    protected Boolean doInBackground(Void... params)
    {
        try
        {
            //AppClassifier.get(mContext).trainClassifier(mContext);
            return Boolean.TRUE;
        }
        catch (Exception e)
        {
            return Boolean.FALSE;
        }
    }


    @Override
    protected void onPostExecute (Boolean result)
    {
        mProgressDialog.dismiss();

        mListener.onTrainingOver(result);
    }
}
