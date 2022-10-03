package com.adventnet.persistence.fos;

public class FOSException extends Exception
{
    private int errorCode;
    
    public FOSException(final int errCode, final String message) {
        super(message);
        this.errorCode = errCode;
    }
    
    public FOSException(final FOSErrorCode errCode, final String message) {
        super(message);
        this.errorCode = errCode.intValue;
    }
    
    public String getErrDesc() {
        final String code = FOSErrorCode.getErrorMsg(this.errorCode);
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
