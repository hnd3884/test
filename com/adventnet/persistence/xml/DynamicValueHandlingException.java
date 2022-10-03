package com.adventnet.persistence.xml;

public class DynamicValueHandlingException extends Exception
{
    public DynamicValueHandlingException(final String msg) {
        super(msg);
    }
    
    public DynamicValueHandlingException(final String msg, final Throwable th) {
        super(msg, th);
    }
}
