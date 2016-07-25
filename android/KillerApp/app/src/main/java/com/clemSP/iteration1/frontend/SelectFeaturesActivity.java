package com.clemSP.iteration1.frontend;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.clemSP.iteration1.R;
import com.clemSP.iteration1.backend.AppAttribute;
import com.clemSP.iteration1.backend.VariableDataset;
import com.clemSP.iteration1.frontend.prediction.GenderPredictionActivity;
import com.clemSP.iteration1.frontend.prediction.WeaponPredictionActivity;

import java.util.ArrayList;
import java.util.List;

public class SelectFeaturesActivity extends AppCompatActivity
{
    private RadioGroup mPredictionGroup;
    private boolean mPredictWeapon;

    private List<CheckBox> mFeaturesBoxes;
    private boolean[] mSelectedFeatures;

    private EditText mTitleField;
    private RadioGroup mPovGroup;
    private Button mYearButton;
    private NumberPicker mYearPicker1, mYearPicker2, mYearPicker3, mYearPicker4;
    private ImageButton mDetectiveButton, mWeaponButton, mVictimButton,
            mSettingButton, mMurdererButton;
    private TextView mDetectiveCaption, mVictimCaption, mWeaponCaption,
            mMurdererCaption, mSettingCaption;

    private ViewFlipper mDialogViewFlipper;
    private TextView mDialogImageCaption;
    private List<Integer> mFlipperIcons;
    private List<String> mFlipperCaptions;

    private int mYear, mPrediction;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_features);

        mFeaturesBoxes = new ArrayList<>(AppAttribute.getNumAttributes());

        mSelectedFeatures = new boolean[AppAttribute.getNumAttributes()];

        selectPrediction();
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
        int id = item.getItemId();
        switch(id)
        {
            case R.id.action_settings: return true;
            /*case R.id.action_weapon:
                startActivity(new Intent(this, WeaponActivity.class));
                finish();
            case R.id.action_gender:
                startActivity(new Intent(this, GenderActivity.class));
                finish();
            case R.id.action_feature: return true;*/
        }
        return super.onOptionsItemSelected(item);
    }


    private void selectPrediction()
    {
        Dialog predictionDialog = new Dialog(this);
        predictionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        predictionDialog.setContentView(R.layout.dialog_select_prediction);

        // Pressing the back button won't close the dialog
        predictionDialog.setCancelable(false);

        mPredictionGroup = (RadioGroup)predictionDialog.findViewById(R.id.prediction_group);

        inflatePredictionsCancelButton(predictionDialog);
        inflatePredictionsOkButton(predictionDialog);

        predictionDialog.show();
    }


    private void inflatePredictionsCancelButton(final Dialog dialog)
    {
        Button cancelButton = (Button) dialog.findViewById(R.id.prediction_cancel_button);

        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
                startActivity(new Intent(SelectFeaturesActivity.this, GenderActivity.class));
                SelectFeaturesActivity.this.finish();
            }
        });
    }


    private void inflatePredictionsOkButton(final Dialog dialog)
    {
        Button okButton = (Button)dialog.findViewById(R.id.prediction_ok_button);

        okButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int selectedId = mPredictionGroup.getCheckedRadioButtonId();

                if(selectedId == R.id.weapon_button)
                {
                    mPredictWeapon = true;
                    mSelectedFeatures[AppAttribute.Weapon.getIndex()] = false;
                }
                else if(selectedId == R.id.gender_button)
                {
                    mPredictWeapon = false;
                    mSelectedFeatures[AppAttribute.Murderer.getIndex()] = false;
                }
                else
                {
                    printErrorToast(R.string.prediction_error);
                    return;
                }

                dialog.dismiss();
                selectFeatures();
            }
        });
    }


    private void selectFeatures()
    {
        Dialog featuresDialog = new Dialog(this);
        featuresDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        featuresDialog.setContentView(R.layout.dialog_features);

        // Pressing the back button won't close the dialog
        featuresDialog.setCancelable(false);

        inflateFeaturesCheckboxes(featuresDialog);
        inflateBackFeaturesButton(featuresDialog);
        inflateFeaturesOkButton(featuresDialog);

        featuresDialog.show();
    }


    private void inflateFeaturesCheckboxes(Dialog dialog)
    {
        int index = -1;
        mFeaturesBoxes.add(++index, (CheckBox)dialog.findViewById(R.id.year_checkbox));
        mFeaturesBoxes.add(++index, (CheckBox)dialog.findViewById(R.id.detective_checkbox));
        mFeaturesBoxes.add(++index, (CheckBox)dialog.findViewById(R.id.setting_checkbox));
        mFeaturesBoxes.add(++index, (CheckBox)dialog.findViewById(R.id.pov_checkbox));

        mFeaturesBoxes.add(++index, (CheckBox)dialog.findViewById(R.id.weapon_checkbox));
        if(mPredictWeapon)
            mFeaturesBoxes.get(index).setVisibility(View.GONE);
        else
            mFeaturesBoxes.get(index).setVisibility(View.VISIBLE);

        mFeaturesBoxes.add(++index, (CheckBox)dialog.findViewById(R.id.victim_checkbox));

        mFeaturesBoxes.add(++index, (CheckBox)dialog.findViewById(R.id.murderer_checkbox));
        if(!mPredictWeapon)
            mFeaturesBoxes.get(index).setVisibility(View.GONE);
        else
            mFeaturesBoxes.get(index).setVisibility(View.VISIBLE);
    }


    private void inflateBackFeaturesButton(final Dialog dialog)
    {
        Button backButton = (Button)dialog.findViewById(R.id.features_back_button);

        backButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
                selectPrediction();
            }
        });
    }


    private void inflateFeaturesOkButton(final Dialog dialog)
    {
        Button okButton = (Button)dialog.findViewById(R.id.features_ok_button);

        okButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int selectedCount = 0;

                for(int index = 0; index < mFeaturesBoxes.size(); index++)
                {
                    CheckBox box = mFeaturesBoxes.get(index);

                    if(box.isChecked())
                    {
                        selectedCount++;
                        mSelectedFeatures[index] = true;
                    }
                }

                if(selectedCount < 2)
                {
                    printErrorToast(R.string.feature_error);
                    return;
                }

                dialog.dismiss();
                layoutActivity();
            }
        });
    }


    private void layoutActivity()
    {
        inflateActivityWidgets();

        inflateActivityYearButton();

        mDetectiveButton = inflateImageButton(R.id.detective_layout,
                R.id.detective_imagebutton, AppAttribute.Detective,
                getString(R.string.detective_image_description));

        mVictimButton = inflateImageButton(R.id.victim_layout,
                R.id.victim_imagebutton, AppAttribute.Victim,
                getString(R.string.victime_image_description));

        if(mPredictWeapon)
            mMurdererButton = inflateImageButton(R.id.weapon_layout,
                    R.id.weapon_imagebutton, AppAttribute.Murderer,
                    getString(R.string.murderer_image_description));
        else
            mWeaponButton = inflateImageButton(R.id.weapon_layout,
                    R.id.weapon_imagebutton, AppAttribute.Weapon,
                    getString(R.string.weapon_image_description));

        mSettingButton = mMurdererButton = inflateImageButton(R.id.setting_layout,
                R.id.setting_imagebutton, AppAttribute.Location,
                getString(R.string.setting_image_description));

        inflateDetectButton();
    }


    private void inflateActivityWidgets()
    {
        mTitleField = (EditText)findViewById(R.id.title_textfield);
        if(mTitleField == null)
            return;
        // Setting hint text
        mTitleField.setHint(R.string.title_label);

        if(mSelectedFeatures[AppAttribute.Pov.getIndex()])
            mPovGroup = (RadioGroup)findViewById(R.id.pov_group);
        else
        {
            RelativeLayout povLayout = (RelativeLayout)findViewById(R.id.pov_layout);
            if(povLayout != null)
                povLayout.setVisibility(View.GONE);
            mPovGroup = null;
        }
    }


    private void inflateActivityYearButton()
    {
        if(!mSelectedFeatures[AppAttribute.Year.getIndex()])
        {
            RelativeLayout yearLayout = (RelativeLayout)findViewById(R.id.year_layout);
            if(yearLayout != null)
                yearLayout.setVisibility(View.GONE);
            mYearButton = null;
            return;
        }

        // Button which opens a dialog to select the year of publication
        mYearButton = (Button) findViewById(R.id.year_button);

        if (mYearButton != null)
            mYearButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Dialog yearDialog = new Dialog(SelectFeaturesActivity.this);
                    yearDialog.setTitle(R.string.year_dialog_title);
                    // Setting custom layout of the dialog
                    yearDialog.setContentView(R.layout.dialog_year);
                    // Pressing the back button won't close the dialog
                    yearDialog.setCancelable(false);

                    inflateYearDialogPickers(yearDialog);
                    inflateYearDialogButton(yearDialog);

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


    private void inflateYearDialogPickers(Dialog dialog)
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


    private void inflateYearDialogButton(final Dialog dialog)
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


    private ImageButton inflateImageButton(int layoutRes, int imageButtonRes,
                                           final AppAttribute attribute, final String description)
    {
        RelativeLayout layout = (RelativeLayout)findViewById(layoutRes);

        if(layout != null)
            if(!mSelectedFeatures[attribute.getIndex()])
                layout.setVisibility(View.GONE);
            else
            {
                ImageButton imageButton = (ImageButton) findViewById(imageButtonRes);
                if(imageButton != null)
                {
                    imageButton.setContentDescription(description);
                    imageButton.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            selectImage(attribute, description);
                        }
                    });
                }
                return imageButton;
            }
        return null;
    }


    private void selectImage(final AppAttribute attribute, String title)
    {
        Dialog selectImageDialog = new Dialog(SelectFeaturesActivity.this);

        selectImageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Setting custom layout of the dialog
        selectImageDialog.setContentView(R.layout.dialog_image_spinner);
        // Pressing the back button won't close the dialog
        selectImageDialog.setCancelable(false);

        inflateImageDialogWidgets(selectImageDialog, title, attribute);
        inflateImageDialogButtons(selectImageDialog);
        inflateImageDialogOkButton(selectImageDialog);

        selectImageDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                int selectedIndex = mDialogViewFlipper.getDisplayedChild();
                int selectedResId = mFlipperIcons.get(selectedIndex);

                switch (attribute)
                {
                    case Detective:
                        mDetectiveButton.setImageResource(selectedResId);
                        mDetectiveCaption = (TextView)findViewById(R.id.detective_textView);
                        mDetectiveCaption.setText(mFlipperCaptions.get(selectedIndex));
                        break;

                    case Victim:
                        mVictimButton.setImageResource(selectedResId);
                        mVictimCaption = (TextView)findViewById(R.id.victim_textView);
                        mVictimCaption.setText(mFlipperCaptions.get(selectedIndex));
                        break;

                    case Weapon:
                        mWeaponButton.setImageResource(selectedResId);
                        mWeaponCaption = (TextView)findViewById(R.id.weapon_textView);
                        mWeaponCaption.setText(mFlipperCaptions.get(selectedIndex));
                        if(mMurdererCaption != null)
                            mMurdererCaption.setText("");
                        break;

                    case Murderer:
                        mMurdererButton.setImageResource(selectedResId);
                        mMurdererCaption = (TextView)findViewById(R.id.weapon_textView);
                        mMurdererCaption.setText(mFlipperCaptions.get(selectedIndex));
                        if(mWeaponCaption != null)
                            mWeaponCaption.setText("");
                        break;

                    case Location:
                        mSettingButton.setImageResource(selectedResId);
                        mSettingCaption = (TextView)findViewById(R.id.setting_textView);
                        mSettingCaption.setText(mFlipperCaptions.get(selectedIndex));
                        break;
                }
            }
        });

        selectImageDialog.show();
    }


    private void inflateImageDialogWidgets(Dialog dialog, String title, AppAttribute attributeIndex)
    {
        TextView titleView = (TextView)dialog.findViewById(R.id.select_image_title);
        titleView.setText(title);

        mDialogViewFlipper = (ViewFlipper)dialog.findViewById(R.id.view_flipper);

        mDialogImageCaption = (TextView) dialog.findViewById(R.id.select_image_caption);

        mFlipperIcons = new ArrayList<>();
        mFlipperIcons.add(R.drawable.unknown);

        mFlipperCaptions = new ArrayList<>();
        mFlipperCaptions.add(getString(R.string.unknown));

        switch (attributeIndex)
        {
            case Detective:
                mFlipperIcons.add(R.drawable.unknown);
                mFlipperCaptions.add(getString(R.string.colonel_race));
                mFlipperIcons.add(R.drawable.detective_hercule_poirot);
                mFlipperCaptions.add(getString(R.string.hercule_poirot));
                mFlipperIcons.add(R.drawable.detective_miss_marple);
                mFlipperCaptions.add(getString(R.string.miss_marple));
                mFlipperIcons.add(R.drawable.detective_mystery_novel);
                mFlipperCaptions.add(getString(R.string.mystery_novel));
                mFlipperIcons.add(R.drawable.detective_superintendent_battle);
                mFlipperCaptions.add(getString(R.string.superintendent_battle));
                mFlipperIcons.add(R.drawable.detective_tommy_tuppence);
                mFlipperCaptions.add(getString(R.string.tommy_tuppence));
                break;

            case Victim:
                mFlipperIcons.add(R.drawable.gender_female);
                mFlipperCaptions.add(getString(R.string.female));
                mFlipperIcons.add(R.drawable.gender_male);
                mFlipperCaptions.add(getString(R.string.male));
                mFlipperIcons.add(R.drawable.unknown);
                mFlipperCaptions.add(getString(R.string.none));
                break;

            case Weapon:
                mFlipperIcons.add(R.drawable.cause_accident);
                mFlipperCaptions.add(getString(R.string.accident));
                mFlipperIcons.add(R.drawable.cause_concussion);
                mFlipperCaptions.add(getString(R.string.concussion));
                mFlipperIcons.add(R.drawable.cause_drowning);
                mFlipperCaptions.add(getString(R.string.drowning));
                mFlipperIcons.add(R.drawable.unknown);
                mFlipperCaptions.add(getString(R.string.none));
                mFlipperIcons.add(R.drawable.cause_poison);
                mFlipperCaptions.add(getString(R.string.poison));
                mFlipperIcons.add(R.drawable.cause_shooting);
                mFlipperCaptions.add(getString(R.string.shooting));
                mFlipperIcons.add(R.drawable.cause_stabbing);
                mFlipperCaptions.add(getString(R.string.stabbing));
                mFlipperIcons.add(R.drawable.cause_strangling);
                mFlipperCaptions.add(getString(R.string.strangling));
                mFlipperIcons.add(R.drawable.cause_throatslit);
                mFlipperCaptions.add(getString(R.string.throatslit));
                mFlipperIcons.add(R.drawable.unknown);
                mFlipperCaptions.add(getString(R.string.error));
                break;

            case Murderer:
                mFlipperIcons.add(R.drawable.gender_female);
                mFlipperCaptions.add(getString(R.string.female));
                mFlipperIcons.add(R.drawable.unknown);
                mFlipperCaptions.add(getString(R.string.lots));
                mFlipperIcons.add(R.drawable.gender_male);
                mFlipperCaptions.add(getString(R.string.male));
                mFlipperIcons.add(R.drawable.unknown);
                mFlipperCaptions.add(getString(R.string.none));
                break;

            case Location:
                mFlipperIcons.add(R.drawable.setting_international);
                mFlipperCaptions.add(getString(R.string.international_label));
                mFlipperIcons.add(R.drawable.setting_uk);
                mFlipperCaptions.add(getString(R.string.uk_label));
        }

        for(int i = 0; i < mFlipperIcons.size(); i++)
        {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(mFlipperIcons.get(i));
            imageView.setBackgroundResource(R.drawable.border_image);
            imageView.setPadding(5,5,5,5);
            imageView.setContentDescription(mFlipperCaptions.get(i));

            mDialogViewFlipper.addView(imageView);
        }

        mDialogImageCaption.setText(mFlipperCaptions.get(mDialogViewFlipper.getDisplayedChild()));
    }


    private void inflateImageDialogButtons(Dialog dialog)
    {
        ImageButton upButton = (ImageButton) dialog.findViewById(R.id.up_button);
        if(upButton == null)
            return;

        upButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                animateUp();
                mDialogImageCaption.setText(mFlipperCaptions.get(mDialogViewFlipper.getDisplayedChild()));
            }
        });

        ImageButton downButton = (ImageButton) dialog.findViewById(R.id.down_button);
        if(downButton == null)
            return;

        downButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                animateDown();
                mDialogImageCaption.setText(mFlipperCaptions.get(mDialogViewFlipper.getDisplayedChild()));
            }
        });
    }


    private void animateUp()
    {
        // Setup animation
        Animation inFromBottom = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromBottom.setInterpolator(new AccelerateInterpolator());
        inFromBottom.setDuration(500);

        Animation outToTop = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -1.0f);
        outToTop.setInterpolator(new AccelerateInterpolator());
        outToTop.setDuration(500);

        mDialogViewFlipper.clearAnimation();
        mDialogViewFlipper.setInAnimation(inFromBottom);
        mDialogViewFlipper.setOutAnimation(outToTop);

        if (mDialogViewFlipper.getDisplayedChild() == mDialogViewFlipper.getChildCount() - 1)
            mDialogViewFlipper.setDisplayedChild(0);
        else
            mDialogViewFlipper.showNext();
    }


    private void animateDown()
    {
        Animation outToBottom = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 1.0f);
        outToBottom.setInterpolator(new AccelerateInterpolator());
        outToBottom.setDuration(500);

        Animation inFromTop = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromTop.setInterpolator(new AccelerateInterpolator());
        inFromTop.setDuration(500);

        mDialogViewFlipper.clearAnimation();
        mDialogViewFlipper.setInAnimation(inFromTop);
        mDialogViewFlipper.setOutAnimation(outToBottom);

        if (mDialogViewFlipper.getDisplayedChild() == 0)
            mDialogViewFlipper.setDisplayedChild(mDialogViewFlipper.getChildCount() - 1);
        else
            mDialogViewFlipper.showPrevious();
    }


    private void inflateImageDialogOkButton(final Dialog dialog)
    {
        Button okButton = (Button) dialog.findViewById(R.id.ok_button);
        if(okButton == null)
            return;

        okButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
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

                    String year = "";
                    if(mSelectedFeatures[AppAttribute.Year.getIndex()])
                    {
                        if (mYear == 0)
                        {
                            printErrorToast(R.string.year_error);
                            return;
                        }
                        else
                            year = "" + mYear;
                    }

                    String pov = "unknown";
                    if(mSelectedFeatures[AppAttribute.Pov.getIndex()])
                    {
                        // Getting checked point of view radio button, if any
                        int selectedPovId = mPovGroup.getCheckedRadioButtonId();
                        RadioButton povButton = (RadioButton) findViewById(selectedPovId);
                        if (povButton == null)
                        {
                            printErrorToast(R.string.pov_error);
                            return;
                        }
                        else
                            pov = povButton.getText().toString();
                    }

                    String detective = "unknown";
                    if(mSelectedFeatures[AppAttribute.Detective.getIndex()])
                    {
                        if("".equals(mDetectiveCaption.getText()))
                        {
                            printErrorToast(R.string.detective_error);
                            return;
                        }
                        else
                            detective = mDetectiveCaption.getText().toString();
                    }

                    String victim = "unknown";
                    if(mSelectedFeatures[AppAttribute.Victim.getIndex()])
                    {
                        if("".equals(mVictimCaption.getText()))
                        {
                            printErrorToast(R.string.victim_error);
                            return;
                        }
                        else
                            victim = mVictimCaption.getText().toString();
                    }

                    String cause = "unknown";
                    if(mSelectedFeatures[AppAttribute.Weapon.getIndex()])
                    {
                        if("".equals(mWeaponCaption.getText()))
                        {
                            printErrorToast(R.string.error);
                            return;
                        }
                        else
                            cause = mWeaponCaption.getText().toString();
                    }

                    String murderer = "unknown";
                    if(mSelectedFeatures[AppAttribute.Murderer.getIndex()])
                    {
                        if("".equals(mMurdererCaption.getText()))
                        {
                            printErrorToast(R.string.gender_error);
                            return;
                        }
                        else
                            murderer = mMurdererCaption.getText().toString();
                    }

                    String setting = "unknown";
                    if(mSelectedFeatures[AppAttribute.Location.getIndex()])
                    {
                        if("".equals(mSettingCaption.getText()))
                        {
                            printErrorToast(R.string.setting_error);
                            return;
                        }
                        else
                            setting = mSettingCaption.getText().toString();
                    }

                    VariableDataset.clear();
                    VariableDataset dataset = VariableDataset.get(
                            SelectFeaturesActivity.this, mPredictWeapon);
                    //dataset.setData(title, year, pov, detective, victim, cause, murderer, setting);
                    showPrediction(dataset);
                }
            });
    }


    private void printErrorToast(int resource)
    {
        Toast.makeText(this, resource, Toast.LENGTH_SHORT).show();
    }


    private void showPrediction(VariableDataset dataset)
    {
        String label = dataset.classify();
        if(mPredictWeapon)
            predictWeapon(label);
        else
            predictGender(label);
    }

    private void predictWeapon(String label)
    {
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

        Intent intent = new Intent(this, WeaponPredictionActivity.class);
        intent.putExtra("dataset", mPrediction);

        startActivityForResult(intent, 0);
    }

    private void predictGender(String label)
    {
        switch (label)
        {
            case "F": mPrediction = R.string.female; break;
            case "M": mPrediction = R.string.male; break;
            case "Lots": mPrediction = R.string.lots; break;
            case "unknown": mPrediction = R.string.unknown; break;
        }

        Intent intent = new Intent(this, GenderPredictionActivity.class);
        intent.putExtra("dataset", mPrediction);

        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode == RESULT_OK)
        {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }
}
