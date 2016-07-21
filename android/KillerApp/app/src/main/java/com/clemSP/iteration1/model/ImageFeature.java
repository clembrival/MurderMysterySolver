package com.clemSP.iteration1.model;


public class ImageFeature
{
    private int mImageRes, mCaptionRes;


    public ImageFeature(int imageRes, int captionRes)
    {
        mImageRes = imageRes;
        mCaptionRes = captionRes;
    }


    public int getImageRes()
    {
        return mImageRes;
    }

    public int getCaptionRes()
    {
        return mCaptionRes;
    }
}
