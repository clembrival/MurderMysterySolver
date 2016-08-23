package com.clemSP.sourcecode.frontend.features_input;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ViewFlipper;


/**
 * Based on tutorial by William J. Francis
 * http://www.techrepublic.com/blog/software-engineer/building-a-slot-machine-in-android-viewflipper-meet-gesture-detector/
 */
public class Wheel
{
    private ViewFlipper mViewFlipper;


    public Wheel(ViewFlipper viewFlipper)
    {
        mViewFlipper = viewFlipper;
    }


    public void animateUp()
    {
        // Setup animation
        Animation inFromBottom = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromBottom.setInterpolator(new AccelerateInterpolator());
        inFromBottom.setDuration(500);

        Animation outToTop = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -1.0f);
        outToTop.setInterpolator(new AccelerateInterpolator());
        outToTop.setDuration(500);

        mViewFlipper.clearAnimation();
        mViewFlipper.setInAnimation(inFromBottom);
        mViewFlipper.setOutAnimation(outToTop);

        if (mViewFlipper.getDisplayedChild() == mViewFlipper.getChildCount() - 1)
            mViewFlipper.setDisplayedChild(0);
        else
            mViewFlipper.showNext();
    }


    public void animateDown()
    {
        Animation outToBottom = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 1.0f);
        outToBottom.setInterpolator(new AccelerateInterpolator());
        outToBottom.setDuration(500);

        Animation inFromTop = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromTop.setInterpolator(new AccelerateInterpolator());
        inFromTop.setDuration(500);

        mViewFlipper.clearAnimation();
        mViewFlipper.setInAnimation(inFromTop);
        mViewFlipper.setOutAnimation(outToBottom);

        if (mViewFlipper.getDisplayedChild() == 0)
            mViewFlipper.setDisplayedChild(mViewFlipper.getChildCount() - 1);
        else
            mViewFlipper.showPrevious();
    }
}
