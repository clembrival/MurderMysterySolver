package com.clemSP.iteration1.frontend.features_selection;

import android.app.Activity;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.clemSP.iteration1.R;
import com.clemSP.iteration1.backend.AttributeFactory.AppAttribute;
import com.clemSP.iteration1.frontend.PredictionSettings;

import java.util.ArrayList;
import java.util.Arrays;


/** Class to build a navigation drawer to select the features. */
public class FeatureDrawer
{
    private DrawerLayout mDrawer;
    private FeatureDrawerListener mListener;

    /** One list for the selectAll/deselectAll buttons, and one for the features radio buttons. */
    private ListView mSelectOptionsList, mFeaturesList;

    /** Adapter for the features, as one of them is hidden every time the drawer is opened,
      * based on the prediction to be made. */
    private ArrayAdapter<String> mFeaturesAdapter;

    /** Reference to the predictions settings, to update the selected features. */
    private PredictionSettings mSettings;


    public interface FeatureDrawerListener
    {
        void onFeatureDrawerClosed(boolean selectionIsValid);
    }


    public FeatureDrawer(Activity activity)
    {
        if(activity instanceof FeatureDrawerListener)
            mListener = (FeatureDrawerListener) activity;

        mSettings = PredictionSettings.getSettings();

        mDrawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        mSelectOptionsList = (ListView) activity.findViewById(R.id.select_drawer);
        mFeaturesList = (ListView) activity.findViewById(R.id.features_drawer);

        // Set selectAll/deselectAll options to first list view
        String[] selectOptions = activity.getResources().getStringArray(R.array.select_array);
        mSelectOptionsList.setAdapter(new ArrayAdapter<>(activity,
        		android.R.layout.simple_list_item_single_choice, selectOptions));

        // Set features entries to second list view
        ArrayList<String> features = new ArrayList<>(Arrays.asList(activity.getResources()
                .getStringArray(R.array.features_array)));
        mFeaturesAdapter = new ArrayAdapter<>(activity,
                android.R.layout.simple_list_item_multiple_choice, features);
        mFeaturesList.setAdapter(mFeaturesAdapter);

        setDrawerLayoutListener(activity);
        setDrawerListListener();
    }


    private void setDrawerLayoutListener(final Activity activity)
    {
        mDrawer.addDrawerListener(new DrawerLayout.DrawerListener()
        {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) { }

            @Override
            public void onDrawerOpened(View drawerView)
            {
                onFeaturesDrawerOpened(activity);
            }

            @Override
            public void onDrawerClosed(View drawerView)
            {
                boolean[] selectedFeatures = getSelectedFeatures();
                boolean selectionIsValid = selectedFeatures != null;

                if(selectionIsValid)
                    mSettings.setSelectedFeatures(selectedFeatures);

            	mListener.onFeatureDrawerClosed(selectionIsValid);
            }

            @Override
            public void onDrawerStateChanged(int newState) { }
        });
    }


    /** Sets the features radio buttons based on whether the corresponding feature is selected. */
    private void onFeaturesDrawerOpened(Activity activity)
    {
        boolean predictWeapon = mSettings.getPredictWeapon();

        int indexToHide = predictWeapon ? AppAttribute.Weapon.getIndex()
                : AppAttribute.Murderer.getIndex();

        int indexToShow = !predictWeapon ? AppAttribute.Weapon.getIndex()
                : AppAttribute.Murderer.getIndex();

        // Hide the feature corresponding to the attribute to be predicted.
        mFeaturesAdapter.remove(activity.getResources()
                .getStringArray(R.array.features_array)[indexToHide]);

        /* If the type of prediction has just changed, show the feature corresponding
         * to the attribute that was previously the target of the prediction. */
        if(mFeaturesAdapter.getCount() < AppAttribute.getNumAttributes() - 1)
            mFeaturesAdapter.insert(activity.getResources()
                    .getStringArray(R.array.features_array)[indexToShow], indexToShow);

        // Update the list view.
        mFeaturesAdapter.notifyDataSetChanged();

        /* listIndex iterates through the list of radio buttons,
         * whereas featuresIndex iterates through the array of features
         * (which has one item additional item, the one hidden in the list). */
        for(int listIndex = 0, featuresIndex = 0; listIndex < mFeaturesList.getCount();
                listIndex++, featuresIndex++)
        {
            // Skip in the features array the element hidden in the list
            if(listIndex == indexToHide)
                featuresIndex++;
            mFeaturesList.setItemChecked(listIndex, mSettings.getFeatureIsSelected(featuresIndex));
        }
    }


    /**
      * @return a boolean array which element contains true iff
      * the corresponding feature was selected.  */
    private boolean[] getSelectedFeatures()
    {
    	boolean[] selectedFeatures = new boolean[mSettings.getSelectedFeaturesLength()];
    	int selectedCount = 0;

    	for(int listIndex = 0, featuresIndex = 0; listIndex < mFeaturesList.getCount();
                listIndex++, featuresIndex++)
    	{
            // Skip in the features array the element hidden in the list
            if(mSettings.getPredictWeapon() && listIndex == AppAttribute.Weapon.getIndex() ||
                    !mSettings.getPredictWeapon() && listIndex == AppAttribute.Murderer.getIndex())
                featuresIndex++;

    		boolean checked = mFeaturesList.isItemChecked(listIndex);

    		selectedFeatures[featuresIndex] = checked;
    		
    		if(checked)
                selectedCount++;
    	}

        // Make sure that the user selected at least two elements.
        if(selectedCount < 2)
            return null;

        return selectedFeatures;
    }


    /** Listener for the selectAll / deselectAll buttons. */
    private void setDrawerListListener()
    {
    	mSelectOptionsList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                /* If 'selectAll' was pressed (position 0), set all the radio buttons to selected,
                 * otherwise, set all the radio buttons to unselected. */
        		for(int index = 0; index < mFeaturesList.getChildCount(); index++)
        			if(mFeaturesList.getChildAt(index).getVisibility() == View.VISIBLE)
        				mFeaturesList.setItemChecked(index, position == 0);
            }
        });
    }
}
