package com.adventnet.customview;

public class CustomViewException extends Exception
{
    public CustomViewException(final String msg, final Throwable rootCause) {
        super(msg, rootCause);
    }
    
    public CustomViewException(final Throwable rootCause) {
        super(rootCause);
    }
    
    public CustomViewException(final String msg) {
        super(msg);
    }
}
