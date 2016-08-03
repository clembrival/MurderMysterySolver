package com.clemSP.iteration1.frontend;

import com.clemSP.iteration1.R;
import com.clemSP.iteration1.frontend.dataset_management.StreamManager;
import com.clemSP.iteration1.frontend.dataset_management.UpdateDatasetActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;

import weka.classifiers.Classifier;


public class WelcomeActivity extends Activity 
{	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_welcome);

		checkInternalFiles();

		startUpdateActivity();
	}


	private void checkInternalFiles()
	{
		final String TAG = "WelcomeActivity";

		String[] assets = {"dataset.arff", "ibk_gender.model", "ibk_weapon.model"};

		for(String asset : assets)
			if(!getBaseContext().getFileStreamPath(asset).exists())
			{
				try
				{
					Log.w(TAG, "Copying assets to internal storage...");
					StreamManager.printStreamToInternalStorage(this,
							getAssets().open(asset), asset, TAG);
				}
				catch (Exception e)
				{
					Log.e(TAG, Arrays.toString(e.getStackTrace()));
				}
			}

		//if(!getBaseContext().getFileStreamPath(asset).exists())
		//{
		/*
			try
			{
				Log.w(TAG, "Copying assets to internal storage...");
				StreamManager.printStreamToInternalStorage(this,
						getAssets().open(assets[0]), assets[0], TAG);

				StreamManager.objectToInternalStorage(this, (Classifier) getAssets().open(assets[1]), assets[1], TAG);
				StreamManager.objectToInternalStorage(this, getAssets().open(assets[2]), assets[2], TAG);
			}
			catch (Exception e)
			{
				Log.e(TAG, Arrays.toString(e.getStackTrace()));
			}
		//}
		*/
	}


	private void startUpdateActivity()
	{
		final TextView titleView = (TextView) findViewById(R.id.title_view);
		
		titleView.postDelayed(new Runnable() 
		{	
			@Override
			public void run() 
			{
				titleView.setVisibility(View.GONE);
				
				startActivity(new Intent(WelcomeActivity.this, UpdateDatasetActivity.class));
                WelcomeActivity.this.finish();				
			}
		}, 3000);
	}	
}
