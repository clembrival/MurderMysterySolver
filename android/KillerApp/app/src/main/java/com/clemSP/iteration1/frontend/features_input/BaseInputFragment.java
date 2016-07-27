package com.clemSP.iteration1.frontend.features_input;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.clemSP.iteration1.frontend.InvalidInputException;
import com.clemSP.iteration1.R;
import com.clemSP.iteration1.backend.AppAttribute;


/**
 * Class containing the methods shared between multiple fragments.
 */
public abstract class BaseInputFragment extends Fragment
{
	protected View mView;
    // Interface implemented by the container activity to communicate with this fragment.
    protected OnFeaturesInputListener mCallback;

    public interface OnFeaturesInputListener
    {
        void onFeaturesInput(String label);
    }

    protected boolean mPredictWeapon;
    protected boolean[] mSelectedFeatures;

    protected EditText mTitleField;
    protected RadioGroup mPovGroup;
    protected Button mYearButton;

    protected int mYear;


    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        // Make sure that the container activity has implemented the callback interface.
        try
        {
            mCallback = (OnFeaturesInputListener) context;
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


    public void setPredictWeapon(boolean predictWeapon)
    {
        mPredictWeapon = predictWeapon;
    }


    public void setSelectedFeatures(boolean[] selectedFeatures)
    {
        mSelectedFeatures = selectedFeatures;
    }


    public void inflateWidgets()
    {
        mTitleField = (EditText) mView.findViewById(R.id.title_textfield);
        if (mTitleField == null)
            return;

        mTitleField.setHint(R.string.title_label);

        RelativeLayout povLayout = (RelativeLayout) mView.findViewById(R.id.pov_layout);
        if (povLayout != null)
        {
            if (mSelectedFeatures[AppAttribute.Pov.getIndex()])
            {
                povLayout.setVisibility(View.VISIBLE);
                mPovGroup = (RadioGroup) mView.findViewById(R.id.pov_group);
            }
            else
            {
                povLayout.setVisibility(View.GONE);
                mPovGroup = null;
            }
        }
    }


    public void inflateYearButton()
    {
        if(!mSelectedFeatures[AppAttribute.Year.getIndex()])
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


    public int getInputYear() throws InvalidInputException
    {
        int year = 0;
        if(mSelectedFeatures[AppAttribute.Year.getIndex()])
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
        if(mSelectedFeatures[attribute.getIndex()])
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
        if(mSelectedFeatures[attribute.getIndex()])
        {
            View spinnerView = spinner.getSelectedView();
            if(R.string.select_label == (int)spinnerView.getId())
                throw new InvalidInputException(errorRes);
            else
                return spinner.toString();
        }
        return "unknown";
    }


    public static void printErrorToast(Context context, String error)
    {
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
    }


    public abstract void update(boolean[] selectedFeatures);
}
