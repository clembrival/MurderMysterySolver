package com.clemSP.sourcecode.frontend.class_selection;

import android.app.Activity;
import android.support.design.widget.TabLayout;
import android.view.View;

import com.clemSP.sourcecode.R;


/** Class to set up tabs for selecting the type of prediction to be made. */
public class ClassTabLayout extends ClassSelector
{
    private TabLayout mTabLayout;
    private TabLayoutListener mListener;

    public interface TabLayoutListener
    {
        void onTabSelected();
    }


    public ClassTabLayout(Activity activity)
    {
        if(activity instanceof TabLayoutListener)
            mListener = (TabLayoutListener) activity;

        mTabLayout = (TabLayout) activity.findViewById(R.id.tab_layout);
        if(mTabLayout == null)
            return;

        mTabLayout.setVisibility(View.VISIBLE);

        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.cause_label));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.murderer_label));

        setTabLayoutListener(mListener);
    }


    private void setTabLayoutListener(final TabLayoutListener listener)
    {
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                updatePredictionTarget(tab.getPosition() == 0);
                listener.onTabSelected();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }
}
