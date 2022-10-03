package com.me.devicemanagement.framework.webclient.quicklink;

public class QuickLinkException extends Exception
{
    int quickLinkErrorCode;
    
    QuickLinkException(final Throwable cause, final int errorcode) {
        super(cause);
        this.quickLinkErrorCode = errorcode;
    }
    
    public int getErrorCode() {
        return this.quickLinkErrorCode;
    }
}
