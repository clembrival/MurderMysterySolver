package com.clemSP.iteration1.frontend;


import com.clemSP.iteration1.backend.AppAttribute;

import java.io.Serializable;

public class ImageFeature implements Serializable
{
    private int mImageRes, mCaptionRes;
    private AppAttribute mAttribute;


    public ImageFeature(int imageRes, int captionRes, AppAttribute attribute)
    {
        mImageRes = imageRes;
        mCaptionRes = captionRes;
        mAttribute = attribute;
    }


    public int getImageRes()
    {
        return mImageRes;
    }

    public void setImageRes(int imageRes)
    {
        mImageRes = imageRes;
    }

    public int getCaptionRes()
    {
        return mCaptionRes;
    }

    public void setCaptionRes(int captionRes)
    {
        mCaptionRes = captionRes;
    }

    public AppAttribute getAttribute()
    {
        return mAttribute;
    }
}
