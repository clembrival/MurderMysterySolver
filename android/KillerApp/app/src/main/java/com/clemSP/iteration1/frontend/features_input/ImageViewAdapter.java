package com.clemSP.iteration1.frontend.features_input;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.clemSP.iteration1.R;
import com.clemSP.iteration1.frontend.ImageFeature;

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
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public long getItemId(int position)
    {
        // TODO Auto-generated method stub
        return 0;
    }


    public void removeItem(int position)
    {
        mResources.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(ImageFeature feature)
    {
        mResources.add(feature);
        notifyDataSetChanged();
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