package com.clemSP.iteration1.frontend;

import com.clemSP.iteration1.R;
import com.clemSP.iteration1.backend.WeaponDataset;
import com.clemSP.iteration1.frontend.prediction.WeaponPredictionActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;


public class WeaponActivity extends AppCompatActivity
{
	private EditText mTitleField;
	private Spinner mDetectiveSpin;
	private RadioGroup mSettingGroup, mPovGroup;
	private Button mYearButton;
	private NumberPicker mYearPicker1, mYearPicker2, mYearPicker3, mYearPicker4;

	private int mYear;
	private int mPrediction;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weapon);

		inflateWidgets();
		inflateYearButton();
		inflateDetectButton();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch(id)
		{
			case R.id.action_settings: return true;
			/*case R.id.action_weapon: return true;
			case R.id.action_gender:
				startActivity(new Intent(this, GenderActivity.class));
				finish();
			case R.id.action_feature:
				startActivity(new Intent(this, SelectFeaturesActivity.class));
				finish();*/
		}
		return super.onOptionsItemSelected(item);
	}
	

	private void inflateWidgets()
	{
		mTitleField = (EditText)findViewById(R.id.title_textfield);
		if(mTitleField == null)
			return;
		// Setting hint text
		mTitleField.setHint(R.string.title_label);

		// String array containing the different detectives
		String[] names = getResources().getStringArray(R.array.detective_array);

		// Adding detective entries and custom layout to spinner
		ArrayAdapter<String> detective_adapter = new ArrayAdapter<>(this,
				R.layout.spinner_layout, names);
		// Setting layout of the drop down menu (appears after touching the spinner)
		detective_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		mDetectiveSpin = (Spinner)findViewById(R.id.detective_spinner);
		if(mDetectiveSpin == null)
			return;
		mDetectiveSpin.setAdapter(detective_adapter);

		mSettingGroup = (RadioGroup)findViewById(R.id.setting_group);
		mPovGroup = (RadioGroup)findViewById(R.id.pov_group);
	}


	private void inflateYearButton()
	{
		// Button which opens a dialog to select the year of publication
		mYearButton = (Button) findViewById(R.id.year_button);

		if (mYearButton != null)
			mYearButton.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					Dialog yearDialog = new Dialog(WeaponActivity.this);
					yearDialog.setTitle(R.string.year_dialog_title);
					// Setting custom layout of the dialog
					yearDialog.setContentView(R.layout.dialog_year);
					// Pressing the back button won't close the dialog
					yearDialog.setCancelable(false);

					inflateDialogPickers(yearDialog);
					inflateDialogButton(yearDialog);

					yearDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
					{
						@Override
						public void onDismiss(DialogInterface dialog) {
							// Set year button to the selected year
							mYearButton.setText(String.valueOf(mYear));
						}
					});

					yearDialog.show();
				}
			});
	}


	private void inflateDialogPickers(Dialog dialog)
	{
		// First digit picker
		mYearPicker1 = (NumberPicker) dialog.findViewById(R.id.year_picker_1);
		mYearPicker1.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		mYearPicker1.setMinValue(1);
		mYearPicker1.setMaxValue(2);
		mYearPicker1.setValue(1);

		// Second digit picker
		mYearPicker2 = (NumberPicker) dialog.findViewById(R.id.year_picker_2);
		mYearPicker2.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		mYearPicker2.setMinValue(0);
		mYearPicker2.setMaxValue(9);
		mYearPicker2.setValue(9);

		// Third digit picker
		mYearPicker3 = (NumberPicker) dialog.findViewById(R.id.year_picker_3);
		mYearPicker3.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		mYearPicker3.setMinValue(0);
		mYearPicker3.setMaxValue(9);
		mYearPicker3.setValue(0);

		// Fourth digit picker
		mYearPicker4 = (NumberPicker) dialog.findViewById(R.id.year_picker_4);
		mYearPicker4.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		mYearPicker4.setMinValue(0);
		mYearPicker4.setMaxValue(9);
		mYearPicker4.setValue(0);
	}


	private void inflateDialogButton(final Dialog dialog)
	{
		// Confirm button on the year of publication dialog
		Button button = (Button) dialog.findViewById(R.id.year_ok_button);
		button.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				mYear = mYearPicker1.getValue() * 1000;
				mYear += mYearPicker2.getValue() * 100;
				mYear += mYearPicker3.getValue() * 10;
				mYear += mYearPicker4.getValue();

				dialog.dismiss();
			}
		});
	}


	private void inflateDetectButton()
	{
		Button detectButton = (Button)findViewById(R.id.detect_button);
		if(detectButton != null)
			detectButton.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					String title = mTitleField.getText().toString();
					if("".equals(title))
					{
						printErrorToast(R.string.title_error);
						return;
					}

					if(mYear == 0)
					{
						printErrorToast(R.string.year_error);
						return;
					}

					String setting;
					// Getting checked setting radio button, if any
					int selectedSettingId = mSettingGroup.getCheckedRadioButtonId();
					RadioButton settingButton = (RadioButton) findViewById(selectedSettingId);
					if (settingButton != null)
						setting = settingButton.getText().toString();
					else
					{
						printErrorToast(R.string.setting_error);
						return;
					}

					String pov;
					// Getting checked point of view radio button, if any
					int selectedPovId = mPovGroup.getCheckedRadioButtonId();
					RadioButton povButton = (RadioButton) findViewById(selectedPovId);
					if (povButton != null)
						pov = povButton.getText().toString();
					else
					{
						printErrorToast(R.string.pov_error);
						return;
					}

					View detective = mDetectiveSpin.getSelectedView();
					if(R.string.select_label == (int)detective.getId())
					{
						printErrorToast(R.string.detective_error);
						return;
					}

					WeaponDataset.clear();
					WeaponDataset weaponDataset = WeaponDataset.get(WeaponActivity.this);
					weaponDataset.setData(title, mYear, setting, pov, detective.toString());
					showPrediction(weaponDataset);
				}
			});
	}

	private void printErrorToast(int resource)
	{
		Toast.makeText(this, resource, Toast.LENGTH_SHORT).show();
	}

	private void showPrediction(WeaponDataset weaponDataset)
	{
		String label = weaponDataset.classify();

		switch (label)
		{
			case "Accident": mPrediction = R.string.accident; break;
			case "Concussion": mPrediction = R.string.concussion; break;
			case "Drowning": mPrediction = R.string.drowning; break;
			case "Poison": mPrediction = R.string.poison; break;
			case "None": mPrediction = R.string.none; break;
			case "Shooting": mPrediction = R.string.shooting; break;
			case "Stabbing": mPrediction = R.string.stabbing; break;
			case "Strangling": mPrediction = R.string.strangling; break;
			case "ThroatSlit": mPrediction = R.string.throatslit; break;
			case "unknown": mPrediction = R.string.unknown; break;
		}

		Intent intent = new Intent(WeaponActivity.this, WeaponPredictionActivity.class);
		intent.putExtra("dataset", mPrediction);

		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// If user confirmed login or registration process, retrieve the corresponding user object
		if(resultCode == RESULT_OK)
		{
			mTitleField.setText("");
			mYearButton.setText(R.string.select_label);
			mDetectiveSpin.setSelection(0);
			mSettingGroup.clearCheck();
			mPovGroup.clearCheck();
		}
	}
}
