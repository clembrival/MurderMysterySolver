package com.clemSP.iteration1.frontend.prediction;

import com.clemSP.iteration1.R;
import com.clemSP.iteration1.backend.AppAttribute;
import com.clemSP.iteration1.frontend.ImageFeature;


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
            case R.string.lots: imageRes = R.drawable.unknown; break;
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
        return R.array.gender_array;
    }
}
