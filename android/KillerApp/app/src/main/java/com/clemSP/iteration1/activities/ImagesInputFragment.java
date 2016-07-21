package com.clemSP.iteration1.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.clemSP.iteration1.InvalidInputException;
import com.clemSP.iteration1.R;
import com.clemSP.iteration1.model.AppAttribute;
import com.clemSP.iteration1.model.ImageFeature;
import com.clemSP.iteration1.model.ImageFeatureManager;
import com.clemSP.iteration1.model.VariableDataset;
import com.clemSP.iteration1.model.Wheel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


/**
 * Fragment containing images-based widgets for the features selected to make the prediction.
 */
public class ImagesInputFragment extends BaseInputFragment
{
    private ImageButton mDetectiveButton, mWeaponButton, mVictimButton,
            mSettingButton, mMurdererButton;
    private TextView mDetectiveCaption, mVictimCaption, mWeaponCaption,
            mMurdererCaption, mSettingCaption, mDialogImageCaption;

    private Wheel mWheel;
    private ViewFlipper mDialogViewFlipper;

    private List<Integer> mFlipperIcons;
    private List<String> mFlipperCaptions;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_images_input, container, false);

        inflateWidgets(view);

        inflateYearButton(view);

        mDetectiveButton = inflateImageButton(view, AppAttribute.Detective, R.id.detective_layout,
                R.id.detective_imagebutton, getString(R.string.detective_image_description));

        mVictimButton = inflateImageButton(view, AppAttribute.Victim, R.id.victim_layout,
                R.id.victim_imagebutton, getString(R.string.victime_image_description));

        if(mPredictWeapon)
            mMurdererButton = inflateImageButton(view, AppAttribute.Murderer, R.id.weapon_layout,
                    R.id.weapon_imagebutton, getString(R.string.murderer_image_description));
        else
            mWeaponButton = inflateImageButton(view, AppAttribute.Weapon, R.id.weapon_layout,
                    R.id.weapon_imagebutton, getString(R.string.weapon_image_description));

        mSettingButton = inflateImageButton(view, AppAttribute.Location, R.id.setting_layout,
                R.id.setting_imagebutton, getString(R.string.setting_image_description));

        inflateDetectButton(view);

        return view;
    }


    private ImageButton inflateImageButton(final View view, final AppAttribute attribute, int layoutRes,
                                           int imageButtonRes, final String description)
    {
        RelativeLayout layout = (RelativeLayout) view.findViewById(layoutRes);

        if(layout != null)
            if(!mSelectedFeatures[attribute.getIndex()])
                layout.setVisibility(View.GONE);
            else
            {
                ImageButton imageButton = (ImageButton) view.findViewById(imageButtonRes);
                if(imageButton != null)
                {
                    imageButton.setContentDescription(description);
                    imageButton.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            selectImage(view, attribute, description);
                        }
                    });
                }
                return imageButton;
            }
        return null;
    }


    private void selectImage(final View view, final AppAttribute attribute, String title)
    {
        Dialog selectImageDialog = new Dialog(view.getContext());

        selectImageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Setting custom layout of the dialog
        selectImageDialog.setContentView(R.layout.dialog_image_spinner);
        // Pressing the back button won't close the dialog
        selectImageDialog.setCancelable(false);

        inflateImageDialogWidgets(view, selectImageDialog, title, attribute);
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
                        mDetectiveCaption = (TextView) view.findViewById(R.id.detective_textView);
                        mDetectiveCaption.setText(mFlipperCaptions.get(selectedIndex));
                        break;

                    case Victim:
                        mVictimButton.setImageResource(selectedResId);
                        mVictimCaption = (TextView) view.findViewById(R.id.victim_textView);
                        mVictimCaption.setText(mFlipperCaptions.get(selectedIndex));
                        break;

                    case Weapon:
                        mWeaponButton.setImageResource(selectedResId);
                        mWeaponCaption = (TextView) view.findViewById(R.id.weapon_textview);
                        mWeaponCaption.setText(mFlipperCaptions.get(selectedIndex));
                        if(mMurdererCaption != null)
                            mMurdererCaption.setText("");
                        break;

                    case Murderer:
                        mMurdererButton.setImageResource(selectedResId);
                        mMurdererCaption = (TextView) view.findViewById(R.id.weapon_textview);
                        mMurdererCaption.setText(mFlipperCaptions.get(selectedIndex));
                        if(mWeaponCaption != null)
                            mWeaponCaption.setText("");
                        break;

                    case Location:
                        mSettingButton.setImageResource(selectedResId);
                        mSettingCaption = (TextView) view.findViewById(R.id.setting_textView);
                        mSettingCaption.setText(mFlipperCaptions.get(selectedIndex));
                        break;
                }
            }
        });

        selectImageDialog.show();
    }


    private void inflateImageDialogWidgets(View view, Dialog dialog, String title, AppAttribute attribute)
    {
        TextView titleView = (TextView)dialog.findViewById(R.id.select_image_title);
        titleView.setText(title);

        mDialogViewFlipper = (ViewFlipper)dialog.findViewById(R.id.view_flipper);
        mWheel = new Wheel(mDialogViewFlipper);

        mDialogImageCaption = (TextView) dialog.findViewById(R.id.select_image_caption);

        mFlipperIcons = new ArrayList<>();
        mFlipperCaptions = new ArrayList<>();

        List<ImageFeature> features = ImageFeatureManager.getRelatedImages(attribute);
        if(features == null)
            return;

        for(ImageFeature feature : features)
        {
            mFlipperIcons.add(feature.getImageRes());
            mFlipperCaptions.add(getString(feature.getCaptionRes()));
        }

        for(int i = 0; i < mFlipperIcons.size(); i++)
        {
            ImageView imageView = new ImageView(view.getContext());
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
                mWheel.animateUp();
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
                mWheel.animateDown();
                mDialogImageCaption.setText(mFlipperCaptions.get(mDialogViewFlipper.getDisplayedChild()));
            }
        });
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





    private void inflateDetectButton(final View view)
    {
        Button detectButton = (Button) view.findViewById(R.id.detect_button);
        if(detectButton != null)
            detectButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    try
                    {
                        String title = mTitleField.getText().toString();
                        if("".equals(title))
                            throw new InvalidInputException(R.string.title_error);

                        int year = getInputYear();

                        String pov = getInputFromRadioGroup(view, AppAttribute.Pov, mPovGroup,
                                R.string.pov_error);

                        String detective = getInputFromImageButton(view, AppAttribute.Detective,
                                mDetectiveCaption, R.string.detective_error);

                        String victim = getInputFromImageButton(view, AppAttribute.Victim,
                                mVictimCaption, R.string.victim_error);

                        String cause = getInputFromImageButton(view, AppAttribute.Weapon,
                                mWeaponCaption, R.string.cause_error);

                        String murderer = getInputFromImageButton(view, AppAttribute.Murderer,
                                mMurdererCaption, R.string.gender_error);

                        String setting = getInputFromImageButton(view, AppAttribute.Location,
                                mSettingCaption, R.string.setting_error);

                        VariableDataset.clear();
                        VariableDataset dataset = VariableDataset.get(view.getContext(), mPredictWeapon);
                        dataset.setData(title, year, pov, detective, victim, cause, murderer, setting);
                        mCallback.onFeaturesInput(dataset.classify());
                    }
                    catch (InvalidInputException iie)
                    {
                        printErrorToast(view, iie.getErrorRes());
                    }
                }
            });
    }


    private String getInputFromImageButton(View view, AppAttribute attribute, TextView caption,
                                           int errorRes)
    {
        if(mSelectedFeatures[attribute.getIndex()])
        {
            if("".equals(caption.getText()))
            {
                printErrorToast(view, errorRes);
                return "";
            }
            else
                return caption.getText().toString();
        }
        return "unknown";
    }


    private void printErrorToast(View view, int resource)
    {
        Toast.makeText(view.getContext(), resource, Toast.LENGTH_SHORT).show();
    }
}
