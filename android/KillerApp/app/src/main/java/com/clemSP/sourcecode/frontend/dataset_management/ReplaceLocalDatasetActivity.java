package com.clemSP.sourcecode.frontend.dataset_management;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;

import com.clemSP.sourcecode.R;
import com.clemSP.sourcecode.frontend.MainActivity;
import com.clemSP.sourcecode.frontend.settings.PreferencesMap;


/** Class creating AsyncTasks to compare the local data set to the one on the server,
  * and update the local one if needed.
  */
public class ReplaceLocalDatasetActivity extends Activity implements DatasetTask.DatasetTaskListener
{
	/** The start of the urls which will be accessed when updating the local dataset. */
	private static final String BASE_URL = "https://murder-mystery-server.herokuapp.com/killerapp/";

	/** Reference to the class storing the preferences shared by the activities. */
	private SharedPreferences mPreferences;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_dataset);

		mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

		if(mPreferences.getBoolean(PreferencesMap.KEY_PREF_SHARE_DATA, true))
			checkNetworkConnection();
		else
			startMainActivity();
	}


	/** Checks whether the app has access to an Internet connection
	 * (via WiFi or mobile data). */
	private void checkNetworkConnection()
	{
		ConnectivityManager connMgr = (ConnectivityManager)
				getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

		if (networkInfo != null && networkInfo.isConnected())
		{
			if(!mPreferences.getBoolean(PreferencesMap.KEY_PREF_FETCH_DATA_AUTO, false))
				showServerDialog();
			else
				new CompareDatasetsTask(ReplaceLocalDatasetActivity.this,
						BASE_URL + "timestamp/").execute();
		}
		else
			showErrorDialog(R.string.network_error);
	}


	/**
	  * Builds the common elements to all dialogs.
	  * @param messageRes the resource containing the string to be printed on the dialog
	  * @return the builder to be used to construct the rest of the dialog
	  */
	private AlertDialog.Builder getDialogBuilder(int messageRes)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(messageRes);
		builder.setCancelable(false);

		return builder;
	}


	/** 
	  * Shows a pop-up dialog to ask the user if they want to update the server
	  * with the correct answer they just input. */
	private void showServerDialog()
	{
		AlertDialog.Builder builder = getDialogBuilder(R.string.fetch_data_question);

		builder.setNegativeButton(R.string.negative_button, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				startMainActivity();
			}
		});

		builder.setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				new CompareDatasetsTask(ReplaceLocalDatasetActivity.this,
						BASE_URL + "timestamp/").execute();
			}
		});

		builder.create().show();
	}


	/** Shows an AlertDialog with an error message and two buttons. */
	private void showErrorDialog(int message)
	{
		AlertDialog.Builder builder = getDialogBuilder(message);

		// Cancel button
		builder.setNegativeButton(R.string.cancel_label, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.cancel();
			}
		});

		// Try again button
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
				startMainActivity();
			}
		});
		errorDialog.show();
	}


	/** Handles the app's behaviour depending on the DatasetTask's outcome. */
	@Override
	public void onTaskCompleted(DatasetTask task, int status)
	{
		if(task instanceof CompareDatasetsTask)
		{
			// status is true if the local dataset is different from the server's
			if (status == DatasetTask.POSITIVE_RESULT)
				new ReplaceDatasetTask(this, BASE_URL + "new_entries/").execute();
			else if(status == DatasetTask.ERROR)
				showErrorDialog(R.string.server_error);
			else
				startMainActivity();
		}
		else if(task instanceof ReplaceDatasetTask)
		{
			// status is true if the local dataset was updated
			if(status == DatasetTask.POSITIVE_RESULT)
				startMainActivity();
			else if(status == DatasetTask.ERROR)
				showErrorDialog(R.string.download_error);
			else
				startMainActivity();
		}
	}


	/** Starts the MainActivity. */
	private void startMainActivity()
	{
		startActivity(new Intent(ReplaceLocalDatasetActivity.this, MainActivity.class));
		finish();
	}
}