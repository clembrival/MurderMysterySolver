package com.clemSP.iteration1.frontend.features_selection;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;

import com.clemSP.iteration1.R;
import com.clemSP.iteration1.backend.AppAttribute;
import com.clemSP.iteration1.frontend.features_input.BaseInputFragment;


public class FeatureDrawer
{
    private AppCompatActivity mActivity;
    private boolean[] mSelectedFeatures;
    private boolean mPredictWeapon;

    private DrawerLayout mDrawer;
    private ListView mDrawerList;


    public FeatureDrawer(AppCompatActivity activity, boolean[] selectedFeatures, boolean predictWeapon)
    {
        mActivity = activity;
        mSelectedFeatures = selectedFeatures;
        mPredictWeapon = predictWeapon;

        mDrawer = (DrawerLayout) mActivity.findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) mActivity.findViewById(R.id.left_drawer);

        String[] features = mActivity.getResources().getStringArray(R.array.features_array);
        mDrawerList.setAdapter(new ArrayAdapter<>(mActivity,
                android.R.layout.simple_list_item_multiple_choice, features));

        setDrawerLayoutListener();
        setDrawerLisstListener();
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
                        mDrawerList.getChildAt(index).setVisibility(View.GONE);
                    else
                    {
                        mDrawerList.getChildAt(index).setVisibility(View.VISIBLE);
                        mDrawerList.setItemChecked(index, mSelectedFeatures[index]);
                    }
            }

            @Override
            public void onDrawerClosed(View drawerView)
            {
                int selectedCount = 0;

                for(int index = 0; index < mDrawerList.getChildCount(); index++)
                    if(mDrawerList.isItemChecked(index))
                        selectedCount++;

                if(selectedCount < 2)
                {
                    BaseInputFragment.printErrorToast(mActivity, mActivity.getString(R.string.feature_error));
                    return;
                }

                BaseInputFragment inputFragment = (BaseInputFragment) mActivity.getSupportFragmentManager()
                        .findFragmentById(R.id.fragment_container);

                if(inputFragment != null)
                    inputFragment.update();
            }

            @Override
            public void onDrawerStateChanged(int newState) { }
        });

    }


    private void setDrawerLisstListener()
    {
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                mSelectedFeatures[position] = ((CheckedTextView) view).isChecked();
            }
        });
    }
}
