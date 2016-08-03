package com.clemSP.iteration1.frontend.dataset_management;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.clemSP.iteration1.R;
import com.clemSP.iteration1.frontend.MainActivity;


/** Class creating asynctasks to compare the local dataset to the one on the server
 *  and update the local one if needed. */
public class UpdateDatasetActivity extends Activity implements DatasetTask.DatasetTaskListener
{
	private final String BASE_URL = "https://murder-mystery-server.herokuapp.com/killerapp/";

	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_dataset);
		
		checkNetworkConnection();
	}
	
	
	private void checkNetworkConnection()
	{
		ConnectivityManager connMgr = (ConnectivityManager) 
				getSystemService(Context.CONNECTIVITY_SERVICE);
		
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		
	    if (networkInfo != null && networkInfo.isConnected())
			new CompareDatasetsTask(this, BASE_URL + "count/").execute();
	    else
	   		showErrorDialog(R.string.network_error);
	}

	
	private void showErrorDialog(int message)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setNegativeButton(R.string.cancel_label, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();               
            }
        });

        builder.setPositiveButton(R.string.try_again_label, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                checkNetworkConnection();
            }
        });

		Dialog errorDialog = builder.create();
		errorDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
		{
			@Override
			public void onCancel(DialogInterface dialog)
			{
				//startMainActivity();
			}
		});
		errorDialog.show();
	}


	@Override
	public void onTaskCompleted(DatasetTask task, boolean status)
	{
		if(task instanceof CompareDatasetsTask)
		{
			if (status)
				new ReplaceDatasetTask(this, BASE_URL + "file/").execute();
			else
				startMainActivity();
		}
		else if(task instanceof ReplaceDatasetTask)
		{
			if(status)
				startMainActivity();
			else
				showErrorDialog(R.string.download_error);
		}
	}


	private void startMainActivity()
	{
		startActivity(new Intent(UpdateDatasetActivity.this, MainActivity.class));
		UpdateDatasetActivity.this.finish();
	}

}
