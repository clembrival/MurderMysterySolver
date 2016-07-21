/*
package com.clemSP.iteration1.activities;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.clemSP.iteration1.R;


public class FeatureAdapter extends BaseAdapter
{
    private Activity mActivity;
    private Integer[] mThumbIds;


    public FeatureAdapter(Activity activity)
    {
        mActivity = activity;
    }

    public int getCount()
    {
        return mThumbIds.length;
    }

    public Object getItem(int position)
    {
        return mThumbIds[position];
    }

    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }


    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent, Activity a)
    {
        View cell;

        if (convertView == null)
        {
            LayoutInflater inflater = mActivity.getLayoutInflater();
            cell = inflater.inflate(R.layout.features_grid_cell, parent, false);
        }
        else
        {
            cell = (ImageView) convertView;
        }

        ImageButton imageButton = (ImageButton) mActivity.findViewById(R.id.cell_imagebutton);
        imageButton.setImageResource(mThumbIds[position]);

        TextView textView = (TextView) mActivity.findViewById(R.id.cell_textView);

        return imageView;
    }
}
*/