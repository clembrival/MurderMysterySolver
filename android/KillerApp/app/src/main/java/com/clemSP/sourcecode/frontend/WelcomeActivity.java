package com.clemSP.sourcecode.frontend;

import com.clemSP.sourcecode.R;
import com.clemSP.sourcecode.frontend.dataset_management.ReplaceLocalDatasetActivity;
import com.clemSP.sourcecode.frontend.dataset_management.StreamManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import java.io.ObjectInputStream;

import weka.classifiers.Classifier;


/** Class which copies the files from the assets folder if necessary,
  * and shows the app's splash screen.  
  */
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


	/** Checks if the assets files are already in internal storage,
	  * and copies them if not. 
	  */
	private void checkInternalFiles()
	{
		final String TAG = "WelcomeActivity";
		final String[] ASSETS = {"dataset.arff", "ibk_gender.model", "ibk_weapon.model"};
		
		for(int index = 0; index < ASSETS.length; index++)
		{
			String asset = ASSETS[index];
			
			// Check if the file already exists in internal storage.
			if(!getBaseContext().getFileStreamPath(asset).exists())
			{
				Log.w(TAG, "Copying " + asset + " to internal storage...");
				try
				{
					// Output data set as a normal file.
					if(index == 0)
						StreamManager.printStreamToInternalStorage(this,
								getAssets().open(ASSETS[0]), ASSETS[0], TAG, MODE_PRIVATE);
					else
					{
						ObjectInputStream inputStream = null;
						try
						{
							// Convert model files to Classifier objects, and output the objects. 
							inputStream = new ObjectInputStream(getAssets().open(asset));
							Classifier classifier = (Classifier) inputStream.readObject();
							StreamManager.classifierToInternalStorage(this, classifier, asset, TAG);
						}
						finally
						{
							if(inputStream != null)	inputStream.close();
						}
					}
				}
				catch (Exception e)
				{
					Log.e(TAG, e.getLocalizedMessage());
				}
			}
		}
	}


	/** Hides the app's title after 3 seconds and starts the ReplaceLocalDatasetActivity. */
	private void startUpdateActivity()
	{
		final TextView titleView = (TextView) findViewById(R.id.title_view);
		
		titleView.postDelayed(new Runnable() 
		{	
			@Override
			public void run() 
			{
				titleView.setVisibility(View.GONE);
				
				startActivity(new Intent(WelcomeActivity.this, ReplaceLocalDatasetActivity.class));
                WelcomeActivity.this.finish();				
			}
		}, 3000);
	}	
}
