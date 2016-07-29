package com.clemSP.iteration1.frontend.features_input;

import java.util.ArrayList;
import java.util.List;

import com.clemSP.iteration1.frontend.InvalidInputException;
import com.clemSP.iteration1.R;
import com.clemSP.iteration1.backend.AppAttribute;
import com.clemSP.iteration1.backend.Data;
import com.clemSP.iteration1.frontend.ImageFeature;
import com.clemSP.iteration1.backend.VariableDataset;

import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;


/**
 * Fragment containing images-based widgets for the features selected to make the prediction.
 */
public class ImagesInputFragment extends BaseInputFragment
{
    private List<ImageFeature> mFragmentFeatures;

    private GridView mFeatureGrid;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        mView = inflater.inflate(R.layout.fragment_images_input, container, false);

        super.inflateWidgets();
        super.inflateYearButton();

        inflateFeatureGrid();
        inflateDetectButton();

        return mView;
    }


    private void inflateFeatureGrid()
    {
        mFragmentFeatures = new ArrayList<>();

        for(int index = 0; index < mSettings.getSelectedFeaturesLength(); index++)
        {
            AppAttribute attribute = AppAttribute.getAttributeFromIndex(index);
            if(attribute != AppAttribute.Pov && attribute != AppAttribute.Year)
                if(mSettings.getFeatureIsSelected(index))
                {
                    int imageRes = R.drawable.unknown;
                    int captionRes = AppAttribute.getLabelRes(attribute);
                    mFragmentFeatures.add(new ImageFeature(imageRes, captionRes, attribute));
                }
        }

        mFeatureGrid = (GridView) mView.findViewById(R.id.feature_gridview);
        mFeatureGrid.setAdapter(new ImageViewAdapter(mView.getContext(), mFragmentFeatures,
                R.layout.feature_layout_fragment));

        mFeatureGrid.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                selectImage(mFragmentFeatures.get(position).getAttribute());
            }
        });
    }


    private void selectImage(final AppAttribute attribute)
    {
        int layoutPref = PreferenceManager.getDefaultSharedPreferences(mView.getContext())
                .getInt(getString(R.string.saved_images_layout), R.id.test_images_grid);

        int layout;

        if(layoutPref == R.id.test_images_grid)
            layout = R.layout.dialog_image_grid;
        else
            layout = R.layout.dialog_image_spinner;

        final ImageFeatureDialog selectImageDialog = new ImageFeatureDialog(mView.getContext(), layout, attribute);

        selectImageDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                updateFeatureInput(attribute, selectImageDialog.getSelectedFeature());
            }
        });

        selectImageDialog.show();
    }


    private void updateFeatureInput(AppAttribute attribute, ImageFeature selectedFeature)
    {
        for(int index = 0; index < mFragmentFeatures.size(); index++)
        {
            if(attribute.equals(mFragmentFeatures.get(index).getAttribute()))
            {
                ImageFeature feature = new ImageFeature(selectedFeature.getImageRes(),
                        selectedFeature.getCaptionRes(), attribute);
                mFragmentFeatures.set(index, feature);

                ((BaseAdapter)mFeatureGrid.getAdapter()).notifyDataSetChanged();
                return;
            }
        }
    }


    private void inflateDetectButton()
    {
        Button detectButton = (Button) mView.findViewById(R.id.detect_button);
        if(detectButton != null)
            detectButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    try
                    {
                        Data data = new Data();

                        data.setTitle(mTitleField.getText().toString());
                        if("".equals(data.getTitle()))
                            throw new InvalidInputException(R.string.title_error);

                        data.setYear("" + getInputYear());

                        data.setPov(getInputFromRadioGroup(AppAttribute.Pov, mPovGroup,
                                R.string.pov_error));

                        for(ImageFeature feature : mFragmentFeatures)
                        {
                            AppAttribute attribute = feature.getAttribute();
                            data.setAttribute(attribute, getInputFromImageFeature(mView, attribute,
                            		mView.getContext().getString(feature.getCaptionRes()),
                                    AppAttribute.getErrorRes(attribute)));
                        }

                        data.setOthers();

                        VariableDataset.clear();
                        VariableDataset dataset = VariableDataset.get(mView.getContext());
                        dataset.setData(data);
                        mListener.onFeaturesInput(dataset.classify());
                    }
                    catch (InvalidInputException iie)
                    {
                    	iie.printToast(mView.getContext());
                    }
                }
            });
    }


    private String getInputFromImageFeature(View view, AppAttribute attribute, String caption,
                                           int errorRes) throws InvalidInputException
    {
        if(mSettings.getFeatureIsSelected(attribute.getIndex()))
        {
            if(view.getContext().getString(AppAttribute.getLabelRes(attribute)).equals(caption))
                throw new InvalidInputException(errorRes);
            else
                return caption;
        }

        return "unknown";
    }


    @Override
    public void update()
    {
        inflateWidgets();
        inflateYearButton();
        inflateFeatureGrid();
    }
}
