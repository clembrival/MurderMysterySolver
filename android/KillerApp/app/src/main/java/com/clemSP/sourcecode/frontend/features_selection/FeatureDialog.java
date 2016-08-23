package com.clemSP.sourcecode.frontend.features_selection;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.clemSP.sourcecode.R;
import com.clemSP.sourcecode.backend.AttributeFactory;
import com.clemSP.sourcecode.frontend.BaseDialog;
import com.clemSP.sourcecode.frontend.PredictionSettings;
import com.clemSP.sourcecode.frontend.class_selection.ClassDialog;

import java.util.ArrayList;
import java.util.List;


public class FeatureDialog extends BaseDialog
{
    private PredictionSettings mSettings;

    private List<CheckBox> mFeaturesBoxes;

    private int mSelectedCount;

    private boolean mHandleClass;


    public FeatureDialog(Context context, int layoutRes, boolean handleClass)
    {
        super(context, layoutRes);

        mFeaturesBoxes = new ArrayList<>(AttributeFactory.AppAttribute.getNumAttributes());

        mSettings = PredictionSettings.getSettings();
        mHandleClass = handleClass;

        if(mHandleClass)
            inflateClassButton();

        inflateFeaturesCheckboxes();
        inflateSelectButtons();
        inflateCancelButton(R.id.features_back_button);
        inflateFeaturesOkButton();

        show();
    }


    private void inflateClassButton()
    {
        final Button classButton = (Button) findViewById(R.id.class_button);

        if(classButton == null)
            return;

        classButton.setVisibility(View.VISIBLE);
        classButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                FeatureDialog.this.hide();
                ClassDialog classDialog = new ClassDialog(mContext);

                classDialog.setOnDismissListener(new OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialog)
                    {
                        FeatureDialog.this.dismiss();
                    }
                });
            }
        });
    }


    private void inflateFeaturesCheckboxes()
    {
        boolean predictWeapon = mSettings.getPredictWeapon();
        for(int index = 0; index < mSettings.getSelectedFeaturesLength(); index++)
        {
            CheckBox box = (CheckBox) findViewById(AttributeFactory.AppAttribute.getCheckboxRes(index));
            if((predictWeapon && index == AttributeFactory.AppAttribute.Weapon.getIndex()) ||
                    (!predictWeapon && index == AttributeFactory.AppAttribute.Murderer.getIndex()))
                box.setVisibility(View.GONE);
            else
                box.setChecked(mSettings.getFeatureIsSelected(index));
            mFeaturesBoxes.add(box);
        }
    }


    private void inflateSelectButtons()
    {
        Button selectallButton = (Button) findViewById(R.id.features_selectall);

        selectallButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                for(CheckBox box : mFeaturesBoxes)
                    box.setChecked(box.getVisibility() == View.VISIBLE);
            }
        });

        Button deselectallButton = (Button) findViewById(R.id.features_deselectall);

        deselectallButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                for(CheckBox box : mFeaturesBoxes)
                    box.setChecked(false);
            }
        });
    }


    private void inflateFeaturesOkButton()
    {
        Button okButton = (Button) findViewById(R.id.features_ok_button);

        okButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mSelectedCount = 0;

                for(int index = 0; index < mFeaturesBoxes.size(); index++)
                {
                    boolean boxChecked = mFeaturesBoxes.get(index).isChecked();

                    if(boxChecked)
                        mSelectedCount++;

                    mSettings.setFeatureIsSelected(index, boxChecked);
                }

                if(mSelectedCount < 2)
                {
                    printErrorToast(mContext, R.string.feature_error);
                    return;
                }
                mCancelled = false;
                dismiss();
            }
        });
    }
}
