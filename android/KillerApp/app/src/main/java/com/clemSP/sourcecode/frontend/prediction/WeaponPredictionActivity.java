package com.clemSP.sourcecode.frontend.prediction;

import com.clemSP.sourcecode.R;
import com.clemSP.sourcecode.backend.AttributeFactory.AppAttribute;
import com.clemSP.sourcecode.frontend.ImageFeature;


public class WeaponPredictionActivity extends PredictionActivity
{
    @Override
    protected void setImageRes(int predictionRes)
    {
        int imageRes;
        int captionRes = predictionRes;

        switch (predictionRes)
        {
            case R.string.accident: imageRes = R.drawable.cause_accident; break;
            case R.string.concussion: imageRes = R.drawable.cause_concussion; break;
            case R.string.drowning: imageRes = R.drawable.cause_drowning; break;
            case R.string.none: imageRes = R.drawable.unknown; break;
            case R.string.poison: imageRes = R.drawable.cause_poison; break;
            case R.string.shooting: imageRes = R.drawable.cause_shooting; break;
            case R.string.stabbing: imageRes = R.drawable.cause_stabbing; break;
            case R.string.strangling: imageRes = R.drawable.cause_strangling; break;
            case R.string.throatslit: imageRes = R.drawable.cause_throatslit; break;
            case R.string.unknown: imageRes = R.drawable.unknown; break;
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
        return R.array.cause_array;
    }
}
