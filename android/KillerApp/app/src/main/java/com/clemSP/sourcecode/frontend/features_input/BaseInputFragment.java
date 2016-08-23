package com.clemSP.sourcecode.frontend.features_input;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.clemSP.sourcecode.frontend.InvalidInputException;
import com.clemSP.sourcecode.R;
import com.clemSP.sourcecode.backend.AttributeFactory.AppAttribute;
import com.clemSP.sourcecode.frontend.PredictionSettings;

import java.io.Serializable;


/**
 * Class containing the methods shared between multiple fragments.
 */
public abstract class BaseInputFragment extends Fragment
{
	protected View mView;

    protected OnFeaturesInputListener mListener;

    // Interface implemented by the container activity to communicate with this fragment.
    public interface OnFeaturesInputListener
    {
        void onFeaturesInput(String label);
    }

    protected PredictionSettings mSettings;

    protected EditText mTitleField;
    protected RadioGroup mPovGroup;
    protected Button mYearButton;
    protected RatingBar mRatingBar;

    protected int mYear;


    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        // Make sure that the container activity has implemented the callback interface.
        try
        {
            mListener = (OnFeaturesInputListener) context;
            mSettings = PredictionSettings.getSettings();
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(context.toString()
                    + " must implement OnClassSelectedListener");
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        if(mTitleField != null)
            outState.putString("title", mTitleField.getText().toString());

        if(mPovGroup != null)
            outState.putInt("pov", mPovGroup.getCheckedRadioButtonId());

        if(mYearButton != null)
            outState.putString("year", mYearButton.getText().toString());

        if(mRatingBar != null)
            outState.putFloat("rating", mRatingBar.getRating());
    }


    public String retrieveSavedString(Bundle savedInstanceState, String key)
    {
        if(savedInstanceState != null)
            return savedInstanceState.getString(key);

        return null;
    }


    public int retrieveSavedInt(Bundle savedInstanceState, String key)
    {
        if(savedInstanceState != null)
            return savedInstanceState.getInt(key);

        return -1;
    }


    public float retrieveSavedFloat(Bundle savedInstanceState, String key)
    {
        if(savedInstanceState != null)
            return savedInstanceState.getFloat(key);

        return -1;
    }


    public Serializable retrieveSavedSerializable(Bundle savedInstanceState, String key)
    {
        if(savedInstanceState != null)
            return savedInstanceState.getSerializable(key);

        return null;
    }


    public void retrieveSavedWidgets(Bundle savedInstanceState)
    {
        String title = retrieveSavedString(savedInstanceState, "title");
        if(mTitleField != null && title != null)
            mTitleField.setText(title);

        int pov = retrieveSavedInt(savedInstanceState, "pov");
        if(mPovGroup != null && pov != -1)
            mPovGroup.check(pov);

        String year = retrieveSavedString(savedInstanceState, "year");
        if(mYearButton != null && year != null)
            mYearButton.setText(year);

        float rating = retrieveSavedFloat(savedInstanceState, "rating");
        if(mRatingBar != null && rating != -1)
            mRatingBar.setRating(rating);
    }


    public void inflateWidgets(boolean clear)
    {
        mTitleField = (EditText) mView.findViewById(R.id.title_textfield);
        if (mTitleField == null)
            return;

        mTitleField.setHint(R.string.title_label);

        if(clear)
            mTitleField.setText("");

        RelativeLayout povLayout = (RelativeLayout) mView.findViewById(R.id.pov_layout);
        if (povLayout != null)
        {
            if (mSettings.getFeatureIsSelected(AppAttribute.Pov.getIndex()))
            {
                povLayout.setVisibility(View.VISIBLE);
                mPovGroup = (RadioGroup) mView.findViewById(R.id.pov_group);
                if(clear && mPovGroup != null)
                    mPovGroup.clearCheck();
            }
            else
            {
                povLayout.setVisibility(View.GONE);
                mPovGroup = null;
            }
        }

        RelativeLayout ratingLayout = (RelativeLayout) mView.findViewById(R.id.rating_layout);
        if(ratingLayout != null)
        {
            if(mSettings.getFeatureIsSelected(AppAttribute.Rating.getIndex()))
            {
                ratingLayout.setVisibility(View.VISIBLE);
                mRatingBar = (RatingBar) mView.findViewById(R.id.rating_bar);
                if(clear && mRatingBar != null)
                    mRatingBar.setRating(0);
            }
            else
            {
                ratingLayout.setVisibility(View.GONE);
                mRatingBar = null;
            }
        }
    }


    public void inflateYearButton(boolean clear)
    {
        if(!mSettings.getFeatureIsSelected(AppAttribute.Year.getIndex()))
        {
            RelativeLayout yearLayout = (RelativeLayout) mView.findViewById(R.id.year_layout);
            if(yearLayout != null)
                yearLayout.setVisibility(View.GONE);
            mYearButton = null;
            return;
        }

        // Button which opens a dialog to select the year of publication
        mYearButton = (Button) mView.findViewById(R.id.year_button);

        if (mYearButton != null)
        {
            if(clear)
            {
                mYearButton.setText(getString(R.string.select_label));
                mYear = 0;
            }

            mYearButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    final YearDialog yearDialog = new YearDialog(mView.getContext());
                    yearDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
                    {
                        @Override
                        public void onDismiss(DialogInterface dialog)
                        {
                            mYear = yearDialog.getYear();
                            // Set year button to the selected year
                            mYearButton.setText(String.valueOf(mYear));
                        }
                    });
                }
            });
        }
    }


    public int getInputYear() throws InvalidInputException
    {
        int year = 0;
        if(mSettings.getFeatureIsSelected(AppAttribute.Year.getIndex()))
        {
            if (mYear == 0)
                throw new InvalidInputException(R.string.year_error);
            else
                year = mYear;
        }
        return year;
    }


    public String getInputFromRadioGroup(AppAttribute attribute, RadioGroup group,
                                         int errorRes) throws InvalidInputException
    {
        if(mSettings.getFeatureIsSelected(attribute.getIndex()))
        {
            // Getting checked point of view radio button, if any
            int selectedId = group.getCheckedRadioButtonId();

            RadioButton radioButton = (RadioButton) mView.findViewById(selectedId);
            if (radioButton == null)
                throw new InvalidInputException(errorRes);
            else
                return radioButton.getText().toString();
        }
        return "unknown";
    }


    public String getInputFromSpinner(AppAttribute attribute, Spinner spinner, int errorRes)
            throws InvalidInputException
    {
        if(mSettings.getFeatureIsSelected(attribute.getIndex()))
        {
            String selectedSpinnerView = spinner.getSelectedItem().toString();
            if(getString(R.string.select_label).equals(selectedSpinnerView))
                throw new InvalidInputException(errorRes);
            else
                return selectedSpinnerView;
        }
        return "unknown";
    }


    public double getInputRating() throws InvalidInputException
    {
        double rating = 0;
        if(mSettings.getFeatureIsSelected(AppAttribute.Rating.getIndex()))
        {
            rating = mRatingBar.getRating();
            if(rating == 0)
                throw new InvalidInputException(R.string.rating_error);
        }
        return rating;
    }


    public abstract void update(boolean clear);
}
