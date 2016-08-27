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


    /** Interface to be implemented by any activity containing a TabLayout. */
    public interface TabLayoutListener
    {
        void onTabSelected();
    }


    /**
     * @param activity the activity containing the TabLayout.
     */
    public ClassTabLayout(Activity activity)
    {
        try
        {
            mListener = (TabLayoutListener) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString()
                    + " must implement TabLayoutListener");
        }

        mTabLayout = (TabLayout) activity.findViewById(R.id.tab_layout);
        if(mTabLayout == null)
            return;

        mTabLayout.setVisibility(View.VISIBLE);

        // Adding a tab for each attribute which can be predicted
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.cause_label));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.murderer_label));

        setTabLayoutListener();
    }


    private void setTabLayoutListener()
    {
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                // tab at position 0 corresponds to cause of death
                ClassTabLayout.super.updatePredictionTarget(tab.getPosition() == 0);

                if(mListener != null)
                    // Notify the listener that a tab was selected
                    mListener.onTabSelected();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }
}
