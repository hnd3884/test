package com.adventnet.cache.exception;

public class CacheException extends Exception
{
    private int errorCode;
    private String errorMsg;
    public static final int UNABLE_TO_PUT = 100;
    public static final int UNABLE_TO_GET = 101;
    public static final int UNABLE_TO_DELETE = 102;
    public static final int UNABLE_TO_PURGE = 103;
    public static final int UNABLE_TO_GETSTATS = 104;
    public static final int INVALID_ARGUMENT = 105;
    public static final int UNSUPPORTED_TYPE = 106;
    public static final int UNKNOWN_POOLNAME = 107;
    
    public CacheException() {
        this.errorCode = -1;
        this.errorMsg = null;
    }
    
    public CacheException(final int errorCode, final String errorMsg) {
        super(errorMsg);
        this.errorCode = -1;
        this.errorMsg = null;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }
    
    public CacheException(final String errorMsg) {
        super(errorMsg);
        this.errorCode = -1;
        this.errorMsg = null;
        this.errorMsg = errorMsg;
    }
    
    public CacheException(final Throwable th) {
        super(th);
        this.errorCode = -1;
        this.errorMsg = null;
    }
    
    public int getErrorCode() {
        return this.errorCode;
    }
    
    public void setErrorCode(final int errorCode) {
        this.errorCode = errorCode;
    }
    
    public String getErrorMsg() {
        return this.errorMsg;
    }
    
    public void setErrorMsg(final String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
