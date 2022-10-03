package com.adventnet.authentication.lm;

public class ADException extends Exception
{
    int errorCode;
    
    public ADException() {
        this.errorCode = -1;
    }
    
    public ADException(final String msg) {
        super(msg);
        this.errorCode = -1;
    }
    
    public ADException(final String msg, final Throwable cause) {
        super(msg, cause);
        this.errorCode = -1;
    }
    
    public void setErrorCode(final int error) {
        this.errorCode = error;
    }
    
    public int getErrorCode() {
        return this.errorCode;
    }
}
