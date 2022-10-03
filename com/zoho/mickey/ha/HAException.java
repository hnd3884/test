package com.zoho.mickey.ha;

public class HAException extends Exception
{
    private int errorCode;
    
    public HAException(final int errCode, final String message) {
        super(message);
        this.errorCode = errCode;
    }
    
    public HAException(final HAErrorCode errCode, final String message) {
        super(message);
        this.errorCode = errCode.intValue;
    }
    
    public HAException(final String message, final Throwable t) {
        super(message, t);
    }
    
    public HAException(final String message) {
        super(message);
    }
    
    public String getErrDesc() {
        final String code = HAErrorCode.getErrorMsg(this.errorCode);
        if (code == null) {
            return "UNKNOWN ERROR CODE";
        }
        return code;
    }
    
    public int getErrCode() {
        return this.errorCode;
    }
    
    @Override
    public String getMessage() {
        final String msg = super.getMessage();
        return "[" + this.getErrCode() + "] [" + this.getErrDesc() + "]. " + ((msg == null) ? "" : msg);
    }
}
