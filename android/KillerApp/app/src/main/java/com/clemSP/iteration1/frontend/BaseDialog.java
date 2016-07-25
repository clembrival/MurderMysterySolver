package com.clemSP.iteration1.frontend;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;


/**
 * Class containing the methods shared between multiple dialogs
 */
public abstract class BaseDialog extends Dialog
{
    /* Need this variable so that the activity that holds the dialog knows
       whether the user pressed 'ok' or 'cancel'. */
    protected boolean mCancelled;


    public BaseDialog(Context context, int layoutRes)
    {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(layoutRes);

        // Pressing the back button won't close the dialog
        setCancelable(false);
    }


    protected void inflateCancelButton(int buttonRes)
    {
        Button cancelButton = (Button) findViewById(buttonRes);

        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mCancelled = true;
                cancel();
            }
        });
    }


    protected void printErrorToast(Context context, int resource)
    {
        Toast.makeText(context, resource, Toast.LENGTH_SHORT).show();
        mCancelled = true;
    }


    public boolean cancelled()
    {
        return mCancelled;
    }
}
