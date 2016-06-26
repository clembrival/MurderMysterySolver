package com.clemSP.iteration1.activities;

import com.clemSP.iteration1.R;
import com.clemSP.iteration1.model.Prediction;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import weka.core.Instances;

public class MainActivity extends AppCompatActivity 
{

	private EditText mTitleField, mYearField;
	private Spinner mDetectiveSpin;
	private RadioGroup mSettingGroup, mPovGroup;
	private RadioButton mUkButton, mInterButton, mFirstButton, mThirdButton;
	private Button mYearButton;

	private Instances mPrediction;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		inflateWidgets();
		inflateButtons();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	

	private void inflateWidgets()
	{
		mTitleField = (EditText)findViewById(R.id.title_textfield);

		mYearField = (EditText)findViewById(R.id.year_textField);
		
		mDetectiveSpin = (Spinner)findViewById(R.id.detective_spinner);
		ArrayAdapter<CharSequence> detective_adapter = ArrayAdapter.createFromResource(this, 
				R.array.detective_arrays, android.R.layout.simple_spinner_item);
		detective_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mDetectiveSpin.setAdapter(detective_adapter);
		mDetectiveSpin.setPromptId(R.string.detective_prompt);
		
		/*mDeathSpin = (Spinner)findViewById(R.id.cause_spinner);
		ArrayAdapter<CharSequence> death_adapter = ArrayAdapter.createFromResource(this, 
				R.array.cause_arrays, android.R.layout.simple_spinner_item);
		death_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mDeathSpin.setAdapter(death_adapter);
		mDeathSpin.setPromptId(R.string.cause_prompt);*/
		
		/*mUkButton = (RadioButton)findViewById(R.id.uk_button);
		mInterButton = (RadioButton)findViewById(R.id.international_button);

		mFirstButton = (RadioButton)findViewById(R.id.first_button);
		mThirdButton = (RadioButton)findViewById(R.id.third_button);*/
		mSettingGroup = (RadioGroup)findViewById(R.id.setting_group);
		mPovGroup = (RadioGroup)findViewById(R.id.pov_group);
	}
	
	
	private void inflateButtons()
	{
		/*mYearButton = (Button)findViewById(R.id.year_button);

		mYearButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Toast.makeText(MainActivity.this, "Pick year", Toast.LENGTH_SHORT).show();
			}
		});*/

		Button detectButton = (Button)findViewById(R.id.detect_button);
		if(detectButton != null)
			detectButton.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					String title = mTitleField.getText().toString();
					int year = Integer.parseInt(mYearField.getText().toString());

					String setting = "";
					int selectedSettingId = mSettingGroup.getCheckedRadioButtonId();
					RadioButton settingButton = (RadioButton) findViewById(selectedSettingId);
					if (settingButton != null)
						setting = settingButton.getText().toString();

					String pov = "";
					int selectedPovId = mPovGroup.getCheckedRadioButtonId();
					RadioButton povButton = (RadioButton) findViewById(selectedPovId);
					if (povButton != null)
						pov = povButton.getText().toString();

					String detective = mDetectiveSpin.getSelectedItem().toString();

					Prediction prediction = new Prediction();
					prediction.setData(title, year, setting, pov, detective);
					mPrediction = prediction.classify(MainActivity.this);

					Intent intent = new Intent(MainActivity.this, PredictionActivity.class);
					intent.putExtra("prediction", mPrediction.classAttribute().value(0));
					startActivity(intent);
				}
			});
	}
}
