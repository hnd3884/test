package com.me.devicemanagement.framework.urlredirection;

public class DMURLRedirectionException extends Exception
{
    int urlRedirectionErrorCode;
    
    DMURLRedirectionException(final Throwable cause, final int errorCode) {
        super(cause);
        this.urlRedirectionErrorCode = errorCode;
    }
    
    public int getErrorCode() {
        return this.urlRedirectionErrorCode;
    }
}
