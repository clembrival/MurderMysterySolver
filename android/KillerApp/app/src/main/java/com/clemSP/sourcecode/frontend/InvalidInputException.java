package com.clemSP.sourcecode.frontend;

import android.content.Context;
import android.widget.Toast;


public class InvalidInputException extends Exception
{
    private int mErrorRes;


    public InvalidInputException(int errorRes)
    {
        mErrorRes = errorRes;
    }

    
    public void printToast(Context context)
    {
    	Toast.makeText(context, mErrorRes, Toast.LENGTH_SHORT).show();
    }
}
