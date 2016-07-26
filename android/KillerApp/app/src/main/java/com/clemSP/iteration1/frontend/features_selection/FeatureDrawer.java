package com.clemSP.iteration1.frontend.features_selection;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.clemSP.iteration1.R;
import com.clemSP.iteration1.backend.AppAttribute;
import com.clemSP.iteration1.frontend.InvalidInputException;
import com.clemSP.iteration1.frontend.features_input.BaseInputFragment;


public class FeatureDrawer
{
    private AppCompatActivity mActivity;
    private boolean[] mSelectedFeatures;
    private boolean mPredictWeapon;

    private DrawerLayout mDrawer;
    private ListView mSelectOptionsList, mFeaturesList;
    

    public FeatureDrawer(AppCompatActivity activity, boolean[] selectedFeatures, boolean predictWeapon)
    {
        mActivity = activity;
        mSelectedFeatures = selectedFeatures;
        mPredictWeapon = predictWeapon;

        mDrawer = (DrawerLayout) mActivity.findViewById(R.id.drawer_layout);
        mSelectOptionsList = (ListView) mActivity.findViewById(R.id.select_drawer);
        mFeaturesList = (ListView) mActivity.findViewById(R.id.features_drawer);

        String[] selectOptions = mActivity.getResources().getStringArray(R.array.select_array);
        mSelectOptionsList.setAdapter(new ArrayAdapter<>(mActivity, 
        		android.R.layout.simple_list_item_single_choice, selectOptions));
        
        String[] features = mActivity.getResources().getStringArray(R.array.features_array);
        mFeaturesList.setAdapter(new ArrayAdapter<>(mActivity,
                android.R.layout.simple_list_item_multiple_choice, features));

        setDrawerLayoutListener();
        setDrawerListListener();
    }


    private void setDrawerLayoutListener()
    {
        mDrawer.addDrawerListener(new DrawerLayout.DrawerListener()
        {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) { }

            @Override
            public void onDrawerOpened(View drawerView)
            {
                for(int index = 0; index < mSelectedFeatures.length; index++)
                    if((index == AppAttribute.Weapon.getIndex() && mPredictWeapon) ||
                            (index == AppAttribute.Murderer.getIndex() && !mPredictWeapon))
                    	mFeaturesList.getChildAt(index).setVisibility(View.INVISIBLE);
                    else
                    {
                    	mFeaturesList.getChildAt(index).setVisibility(View.VISIBLE);
                    	mFeaturesList.setItemChecked(index, mSelectedFeatures[index]);
                    }
            }

            @Override
            public void onDrawerClosed(View drawerView)
            {
            	try
            	{
            		boolean[] selectedFeatures = getSelectedFeatures(); 
            		for(int index = 0; index < selectedFeatures.length; index++)
            			mSelectedFeatures[index] = selectedFeatures[index];
                	
                    BaseInputFragment inputFragment = (BaseInputFragment) mActivity.getSupportFragmentManager()
                            .findFragmentById(R.id.fragment_container);

                    if(inputFragment != null)
                        inputFragment.update();

            	}
            	catch(InvalidInputException iie)
            	{
            		iie.printToast(mActivity);
            	}
            }

            @Override
            public void onDrawerStateChanged(int newState) { }
        });

    }

    
    private boolean[] getSelectedFeatures() throws InvalidInputException
    {
    	boolean[] selectedFeatures = new boolean[mSelectedFeatures.length];
    	int selectedCount = 0;
    	
    	for(int index = 0; index < mFeaturesList.getChildCount(); index++)
    	{
    		boolean checked = mFeaturesList.isItemChecked(index);
    		
    		selectedFeatures[index] = checked;
    		
    		if(checked)
                selectedCount++;
    	}

        if(selectedCount < 2)
            throw new InvalidInputException(R.string.feature_error);
        
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
