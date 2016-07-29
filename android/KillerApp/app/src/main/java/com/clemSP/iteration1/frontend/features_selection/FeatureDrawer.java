package com.clemSP.iteration1.frontend.features_selection;

import android.app.Activity;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.clemSP.iteration1.R;
import com.clemSP.iteration1.backend.AppAttribute;
import com.clemSP.iteration1.frontend.PredictionSettings;

import java.util.ArrayList;
import java.util.Arrays;


/** Class to build a navigation drawer to select the features. */
public class FeatureDrawer
{
    private DrawerLayout mDrawer;
    private ListView mSelectOptionsList, mFeaturesList;
    private ArrayAdapter<String> mFeaturesAdapter;

    private PredictionSettings mSettings;

    private FeatureDrawerListener mListener;


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

        String[] selectOptions = activity.getResources().getStringArray(R.array.select_array);
        mSelectOptionsList.setAdapter(new ArrayAdapter<>(activity,
        		android.R.layout.simple_list_item_single_choice, selectOptions));

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
                onFeaturesDraweredOpened(activity);
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


    private void onFeaturesDraweredOpened(Activity activity)
    {
        boolean predictWeapon = mSettings.getPredictWeapon();

        int indexToHide = predictWeapon ? AppAttribute.Weapon.getIndex()
                : AppAttribute.Murderer.getIndex();

        int indexToShow = !predictWeapon ? AppAttribute.Weapon.getIndex()
                : AppAttribute.Murderer.getIndex();

        int indexToFill = predictWeapon ? indexToShow-1 : indexToShow;

        mFeaturesAdapter.remove(activity.getResources()
                .getStringArray(R.array.features_array)[indexToHide]);

        if(mFeaturesAdapter.getCount() < AppAttribute.getNumAttributes() - 1)
            mFeaturesAdapter.insert(activity.getResources()
                    .getStringArray(R.array.features_array)[indexToShow], indexToFill);

        mFeaturesAdapter.notifyDataSetChanged();
        int listSize = mFeaturesList.getCount();

        for(int index = 0, arrayIndex = 0; index < listSize; index++, arrayIndex++)
            mFeaturesList.setItemChecked(index, mSettings.getFeatureIsSelected(arrayIndex));
    }

    
    private boolean[] getSelectedFeatures()
    {
    	boolean[] selectedFeatures = new boolean[mSettings.getSelectedFeaturesLength()];
    	int selectedCount = 0;

    	for(int index = 0, arrayIndex = 0; index < mFeaturesList.getCount(); index++, arrayIndex++)
    	{
            if(mSettings.getPredictWeapon() && index == AppAttribute.Weapon.getIndex())
                arrayIndex++;

    		boolean checked = mFeaturesList.isItemChecked(index);

    		selectedFeatures[arrayIndex] = checked;
    		
    		if(checked)
                selectedCount++;
    	}

        if(selectedCount < 2)
            return null;

        return selectedFeatures;
    }

    
    private void setDrawerListListener()
    {
    	mSelectOptionsList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {          	
        		for(int index = 0; index < mFeaturesList.getChildCount(); index++)
        			if(mFeaturesList.getChildAt(index).getVisibility() == View.VISIBLE)
        				mFeaturesList.setItemChecked(index, position == 0);
            }
        });
    }
}
