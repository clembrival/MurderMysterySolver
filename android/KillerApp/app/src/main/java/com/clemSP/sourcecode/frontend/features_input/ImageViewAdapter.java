package com.clemSP.sourcecode.frontend.features_input;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.clemSP.sourcecode.R;
import com.clemSP.sourcecode.frontend.ImageFeature;

import java.util.List;


public class ImageViewAdapter extends BaseAdapter
{
    private Context mContext;
    private List<ImageFeature> mResources;
    private int mFeatureLayout;


    public ImageViewAdapter(Context context, List<ImageFeature> resources, int featureLayout)
    {
        mContext = context;
        mResources = resources;
        mFeatureLayout = featureLayout;
    }


    @Override
    public int getCount()
    {
        return mResources.size();
    }


    @Override
    public Object getItem(int position)
    {
        return null;
    }


    @Override
    public long getItemId(int position)
    {
        return 0;
    }


    private class ViewHolder
    {
        TextView textView;
        ImageView imageView;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;

        if(convertView == null)
        {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) mContext.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(mFeatureLayout, parent, false);

            holder.textView = (TextView) convertView.findViewById(R.id.feature_textview);

            holder.imageView = (ImageView) convertView.findViewById(R.id.feature_imageview);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        ImageFeature feature = mResources.get(position);
        holder.textView.setText(feature.getCaptionRes());

        holder.imageView.setImageResource(feature.getImageRes());
        holder.imageView.setBackgroundResource(R.drawable.border_image);
        holder.imageView.setAdjustViewBounds(true);
        holder.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        holder.imageView.setContentDescription(mContext.getString(feature.getCaptionRes()));

        return convertView;
    }
}
