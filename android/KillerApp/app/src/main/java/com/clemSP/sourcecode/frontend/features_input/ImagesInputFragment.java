package com.clemSP.sourcecode.frontend.features_input;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.clemSP.sourcecode.backend.Dataset;
import com.clemSP.sourcecode.frontend.InvalidInputException;
import com.clemSP.sourcecode.R;
import com.clemSP.sourcecode.backend.AttributeFactory.AppAttribute;
import com.clemSP.sourcecode.backend.Data;
import com.clemSP.sourcecode.frontend.ImageFeature;
import com.clemSP.sourcecode.frontend.settings.PreferencesMap;

import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
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

        super.inflateWidgets(true);
        super.inflateYearButton(true);

        inflateFeatureGrid();
        inflateDetectButton();

        return mView;
    }


    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        if(mFragmentFeatures != null)
        {
            for(int index = 0; index < mFragmentFeatures.size(); index++)
            {
                if(mFragmentFeatures.get(index) != null)
                    outState.putSerializable("feature"+index, mFragmentFeatures.get(index));
            }
        }
    }


    @Override
    public void retrieveSavedWidgets(Bundle savedInstanceState)
    {
        super.retrieveSavedWidgets(savedInstanceState);

        if(mFragmentFeatures != null)
        {
            for(int index = 0; index < mFragmentFeatures.size(); index++)
            {
                Serializable feature = retrieveSavedSerializable(savedInstanceState, "feature"+index);
                if(mFragmentFeatures.get(index) != null && feature != null)
                    mFragmentFeatures.set(index, (ImageFeature) feature);
            }
        }
    }


    private void inflateFeatureGrid()
    {
        mFragmentFeatures = new ArrayList<>();

        for(int index = 0; index < mSettings.getSelectedFeaturesLength(); index++)
        {
            AppAttribute attribute = AppAttribute.getAttributeFromIndex(index);
            if(attribute != AppAttribute.Pov && attribute != AppAttribute.Year
                    && attribute != AppAttribute.Rating)
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
        String layoutPref = PreferenceManager.getDefaultSharedPreferences(mView.getContext())
                .getString(PreferencesMap.KEY_PREF_IMAGES_LAYOUT,
                        getString(R.string.pref_imagesGrid));

        int layout;

        if(getString(R.string.pref_imagesGrid).equals(layoutPref))
            layout = R.layout.dialog_image_grid;
        else
            layout = R.layout.dialog_image_spinner;

        final ImageFeatureDialog selectImageDialog = new ImageFeatureDialog(mView.getContext(),
                layout, attribute);

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

                        data.setRating("" + getInputRating());

                        for(ImageFeature feature : mFragmentFeatures)
                        {
                            AppAttribute attribute = feature.getAttribute();
                            String attributeValue = getInputFromImageFeature(mView, attribute,
                                    mView.getContext().getString(feature.getCaptionRes()),
                                    AppAttribute.getErrorRes(attribute));
                            if(getString(R.string.female).equals(attributeValue))
                                attributeValue = "F";
                            else if(getString(R.string.male).equals(attributeValue))
                                attributeValue = "M";

                            if(attribute == AppAttribute.Weapon)
                                attributeValue = attributeValue.replaceAll(" ","");

                            data.setAttribute(attribute, attributeValue);
                        }

                        data.setOthers();

                        Log.w("INPUT", data.toString());

                        Dataset.clear();
                        Dataset dataset = Dataset.get(getActivity());
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
            {
                if(caption.equals(getString(R.string.unknown)))
                    return caption.toLowerCase();
                return caption;
            }
        }

        return "unknown";
    }


    @Override
    public void update(boolean clear)
    {
        inflateWidgets(clear);
        inflateYearButton(clear);
        inflateFeatureGrid();
    }
}
