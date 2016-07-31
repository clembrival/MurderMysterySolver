package com.clemSP.iteration1.frontend.class_selection;

import android.app.Activity;
import android.support.design.widget.TabLayout;
import android.view.View;

import com.clemSP.iteration1.R;
import com.clemSP.iteration1.backend.AttributeFactory.AppAttribute;
import com.clemSP.iteration1.frontend.PredictionSettings;


public class ClassTabLayout
{
    private TabLayout mTabLayout;
    private TabLayoutListener mListener;

    public interface TabLayoutListener
    {
        void onTabSelected();
    }


    public ClassTabLayout(Activity activity)
    {
        mTabLayout = (TabLayout) activity.findViewById(R.id.tab_layout);
        if(mTabLayout == null)
            return;

        mTabLayout.setVisibility(View.VISIBLE);

        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.cause_label));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.murderer_label));

        if(activity instanceof TabLayoutListener)
            mListener = (TabLayoutListener) activity;

        setTabLayoutListener(mListener);
    }


    private void setTabLayoutListener(final TabLayoutListener listener)
    {
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                PredictionSettings settings = PredictionSettings.getSettings();

                int weaponIndex = AppAttribute.Weapon.getIndex();
                int murdererIndex = AppAttribute.Murderer.getIndex();

                if(settings.getPredictWeapon())
                {
                    settings.setFeatureIsSelected(murdererIndex, settings.getFeatureIsSelected(weaponIndex));
                    settings.setFeatureIsSelected(weaponIndex, false);
                }
                else
                {
                    settings.setFeatureIsSelected(weaponIndex, settings.getFeatureIsSelected(murdererIndex));
                    settings.setFeatureIsSelected(murdererIndex, false);
                }
                listener.onTabSelected();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }
}
