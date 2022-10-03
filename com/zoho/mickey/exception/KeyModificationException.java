package com.zoho.mickey.exception;

public class KeyModificationException extends Exception
{
    public KeyModificationException(final String str, final Throwable cause) {
        super(str, cause);
    }
    
    public KeyModificationException(final String str) {
        super(str);
    }
    
    public KeyModificationException(final Throwable cause) {
        super(cause);
    }
}
