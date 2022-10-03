package com.adventnet.customview.service;

import com.adventnet.customview.CustomViewException;

public class ServiceException extends CustomViewException
{
    public ServiceException(final String msg, final Throwable rootCause) {
        super(msg, rootCause);
    }
    
    public ServiceException(final Throwable rootCause) {
        super(rootCause);
    }
    
    public ServiceException(final String msg) {
        super(msg);
    }
}
