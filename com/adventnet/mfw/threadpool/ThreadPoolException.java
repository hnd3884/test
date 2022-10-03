package com.adventnet.mfw.threadpool;

public class ThreadPoolException extends Exception
{
    private Integer errorCode;
    private String message;
    
    public ThreadPoolException() {
    }
    
    public ThreadPoolException(final String s) {
        super(s);
    }
    
    public ThreadPoolException(final Throwable cause) {
        super(cause);
    }
    
    public ThreadPoolException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public ThreadPoolException(final ThreadPoolErrorCodes errorCode, final String message) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.getCode();
        this.message = message;
    }
    
    public ThreadPoolException(final ThreadPoolErrorCodes errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.getCode();
    }
    
    public Integer getErrorCode() {
        return this.errorCode;
    }
    
    @Override
    public String getMessage() {
        return this.message;
    }
    
    public enum ThreadPoolErrorCodes
    {
        INVALID_POOLNAME(2000, "Thread pool does not exist for pool name"), 
        INVALID_MAXPOOLSIZE(2001, "MaxPoolSize is lesser than core pool size");
        
        private final int code;
        private final String msg;
        
        private ThreadPoolErrorCodes(final int code, final String msg) {
            this.code = code;
            this.msg = msg;
        }
        
        public int getCode() {
            return this.code;
        }
        
        public String getMessage() {
            return this.msg;
        }
    }
}
