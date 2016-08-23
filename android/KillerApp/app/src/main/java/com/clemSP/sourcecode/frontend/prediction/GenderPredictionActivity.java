package com.clemSP.sourcecode.frontend.prediction;

import com.clemSP.sourcecode.R;
import com.clemSP.sourcecode.backend.AttributeFactory.AppAttribute;
import com.clemSP.sourcecode.frontend.ImageFeature;


public class GenderPredictionActivity extends PredictionActivity
{
    @Override
    protected void setImageRes(int predictionRes)
    {
        int imageRes;
        int captionRes = predictionRes;

        switch (predictionRes)
        {
            case R.string.female: imageRes = R.drawable.gender_female; break;
            case R.string.lots: imageRes = R.drawable.gender_lots; break;
            case R.string.male: imageRes = R.drawable.gender_male; break;
            case R.string.none: imageRes = R.drawable.unknown; break;
            default:
                imageRes = R.drawable.unknown;
                captionRes = R.string.error;
                break;
        }
        mPrediction = new ImageFeature(imageRes, captionRes, AppAttribute.Murderer);
    }


    @Override
    protected int getSpinnerEntries()
    {
        return R.array.murderer_array;
    }
}
