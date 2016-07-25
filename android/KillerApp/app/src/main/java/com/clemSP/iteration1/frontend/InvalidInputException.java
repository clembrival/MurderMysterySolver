package com.clemSP.iteration1.frontend;


public class InvalidInputException extends Exception
{
    private int mErrorRes;


    public InvalidInputException(int errorRes)
    {
        mErrorRes = errorRes;
    }


    public int getErrorRes()
    {
        return mErrorRes;
    }
}
