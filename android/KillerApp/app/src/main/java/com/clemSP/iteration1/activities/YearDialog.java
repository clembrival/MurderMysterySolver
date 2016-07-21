package com.clemSP.iteration1.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import com.clemSP.iteration1.R;

/**
 * Created by Clem on 17/07/2016.
 */
public class YearDialog extends Dialog
{
    private NumberPicker mYearPicker1, mYearPicker2, mYearPicker3, mYearPicker4;

    private int mYear;

    public YearDialog(Context context)
    {
        super(context);

        setTitle(R.string.year_dialog_title);
        // Setting custom layout of the dialog
        setContentView(R.layout.dialog_year);
        // Pressing the back button won't close the dialog
        setCancelable(false);

        inflateYearDialogPickers();
        inflateYearDialogButton();

        show();
    }

    private void inflateYearDialogPickers()
    {
        // First digit picker
        mYearPicker1 = (NumberPicker) findViewById(R.id.year_picker_1);
        mYearPicker1.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mYearPicker1.setMinValue(1);
        mYearPicker1.setMaxValue(2);
        mYearPicker1.setValue(1);

        // Second digit picker
        mYearPicker2 = (NumberPicker) findViewById(R.id.year_picker_2);
        mYearPicker2.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mYearPicker2.setMinValue(0);
        mYearPicker2.setMaxValue(9);
        mYearPicker2.setValue(9);

        // Third digit picker
        mYearPicker3 = (NumberPicker) findViewById(R.id.year_picker_3);
        mYearPicker3.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mYearPicker3.setMinValue(0);
        mYearPicker3.setMaxValue(9);
        mYearPicker3.setValue(0);

        // Fourth digit picker
        mYearPicker4 = (NumberPicker) findViewById(R.id.year_picker_4);
        mYearPicker4.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mYearPicker4.setMinValue(0);
        mYearPicker4.setMaxValue(9);
        mYearPicker4.setValue(0);
    }


    private void inflateYearDialogButton()
    {
        // Confirm button on the year of publication dialog
        Button button = (Button) findViewById(R.id.year_ok_button);
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                mYear = mYearPicker1.getValue() * 1000;
                mYear += mYearPicker2.getValue() * 100;
                mYear += mYearPicker3.getValue() * 10;
                mYear += mYearPicker4.getValue();

                dismiss();
            }
        });
    }


    public int getYear()
    {
        return mYear;
    }
}
