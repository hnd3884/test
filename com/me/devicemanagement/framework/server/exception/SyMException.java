package com.me.devicemanagement.framework.server.exception;

public class SyMException extends Exception
{
    protected int errorCode;
    private String errorKey;
    private Object[] errorI18NKeyParam;
    private Object errorInfo;
    
    public SyMException() {
        this.errorCode = 1001;
        this.errorKey = null;
        this.errorInfo = null;
    }
    
    public SyMException(final int errorCode, final Throwable cause) {
        super(cause);
        this.errorCode = 1001;
        this.errorKey = null;
        this.errorInfo = null;
        this.errorCode = errorCode;
    }
    
    public SyMException(final int errorCode, final String errorMsg, final Throwable cause) {
        super(errorMsg, cause);
        this.errorCode = 1001;
        this.errorKey = null;
        this.errorInfo = null;
        this.errorCode = errorCode;
    }
    
    public SyMException(final int errorCode, final String errorMsg, final String errorKey, final Throwable cause) {
        super(errorMsg, cause);
        this.errorCode = 1001;
        this.errorKey = null;
        this.errorInfo = null;
        this.errorCode = errorCode;
        this.errorKey = errorKey;
    }
    
    public Object[] getErrorParams() {
        return this.errorI18NKeyParam;
    }
    
    public SyMException(final int errorCode, final String errorMsg, final String errorI18NKey, final Object[] errorI18NKeyParam, final Throwable cause) {
        super(errorMsg, cause);
        this.errorCode = 1001;
        this.errorKey = null;
        this.errorInfo = null;
        this.errorCode = errorCode;
        this.errorKey = errorI18NKey;
        this.errorI18NKeyParam = errorI18NKeyParam;
    }
    
    public int getErrorCode() {
        return this.errorCode;
    }
    
    public String getErrorKey() {
        return this.errorKey;
    }
    
    public void setErrorInfo(final Object errorInfo) {
        this.errorInfo = errorInfo;
    }
    
    public Object getErrorInfo() {
        return this.errorInfo;
    }
}
