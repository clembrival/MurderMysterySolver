package com.clemSP.iteration1.frontend.features_input;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.clemSP.iteration1.R;
import com.clemSP.iteration1.backend.AppAttribute;
import com.clemSP.iteration1.frontend.BaseDialog;
import com.clemSP.iteration1.frontend.ImageFeature;

import java.util.List;


public class ImageFeatureDialog extends BaseDialog
{
    private List<ImageFeature> mDialogFeatures;
    private ImageFeature mSelectedFeature;

    private TextView mDialogImageCaption;

    private Wheel mWheel;
    private ViewFlipper mDialogViewFlipper;


    public ImageFeatureDialog(Context context, int layout, AppAttribute attribute)
    {
        super(context, layout);

        TextView titleView = (TextView) findViewById(R.id.select_image_title);
        titleView.setText(AppAttribute.getLabelRes(attribute));

        // List of ImageFeature objects containing the images and captions to be displayed
        mDialogFeatures = ImageFeatureManager.getRelatedImages(attribute);
        if(mDialogFeatures == null)
            return;

        setLayout(context, layout);
    }


    private void setLayout(Context context, int layout)
    {
        if(layout == R.layout.dialog_image_spinner)
        {
            inflateSpinnerDialogWidgets(context);
            inflateSpinnerDialogButtons();
            inflateImageDialogOkButton();
        }
        else
        {
            inflateGridDialogWidgets(context);
        }
    }


    private void inflateSpinnerDialogWidgets(Context context)
    {
        mDialogViewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);
        mWheel = new Wheel(mDialogViewFlipper);

        mDialogImageCaption = (TextView) findViewById(R.id.select_image_caption);

        for(ImageFeature feature : mDialogFeatures)
        {
            ImageView imageView = new ImageView(context);
            imageView.setImageResource(feature.getImageRes());
            imageView.setBackgroundResource(R.drawable.border_image);
            imageView.setPadding(5,5,5,5);
            imageView.setContentDescription(context.getString(feature.getCaptionRes()));

            mDialogViewFlipper.addView(imageView);
        }

        mDialogImageCaption.setText(mDialogFeatures.get(
                mDialogViewFlipper.getDisplayedChild()).getCaptionRes());
    }


    private void inflateSpinnerDialogButtons()
    {
        ImageButton upButton = (ImageButton) findViewById(R.id.up_button);
        if(upButton == null)
            return;

        upButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mWheel.animateUp();
                mDialogImageCaption.setText(mDialogFeatures.get(
                        mDialogViewFlipper.getDisplayedChild()).getCaptionRes());
            }
        });

        ImageButton downButton = (ImageButton) findViewById(R.id.down_button);
        if(downButton == null)
            return;

        downButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mWheel.animateDown();
                mDialogImageCaption.setText(mDialogFeatures.get(
                        mDialogViewFlipper.getDisplayedChild()).getCaptionRes());
            }
        });
    }


    private void inflateImageDialogOkButton()
    {
        Button okButton = (Button) findViewById(R.id.ok_button);
        if(okButton == null)
            return;

        okButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mSelectedFeature = mDialogFeatures.get(mDialogViewFlipper.getDisplayedChild());
                dismiss();
            }
        });
    }


    private void inflateGridDialogWidgets(Context context)
    {
        GridView gridView = (GridView) findViewById(R.id.image_gridview);
        gridView.setAdapter(new ImageViewAdapter(context, mDialogFeatures,
                R.layout.feature_layout_grid));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                mSelectedFeature = mDialogFeatures.get(position);
                dismiss();
            }
        });
    }


    public ImageFeature getSelectedFeature()
    {
        return mSelectedFeature;
    }
}
