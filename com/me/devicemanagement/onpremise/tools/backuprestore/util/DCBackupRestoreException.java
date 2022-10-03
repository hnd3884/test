package com.me.devicemanagement.onpremise.tools.backuprestore.util;

public class DCBackupRestoreException extends Exception
{
    private int errorCode;
    private Object errorInfo;
    private String errorMessage;
    private String errorDetail;
    
    public DCBackupRestoreException() {
        this.errorCode = -1;
        this.errorInfo = null;
        this.errorMessage = null;
        this.errorDetail = null;
    }
    
    public DCBackupRestoreException(final String msg) {
        super(msg);
        this.errorCode = -1;
        this.errorInfo = null;
        this.errorMessage = null;
        this.errorDetail = null;
    }
    
    public DCBackupRestoreException(final int errorCode, final Throwable cause) {
        super(cause);
        this.errorCode = -1;
        this.errorInfo = null;
        this.errorMessage = null;
        this.errorDetail = null;
        this.errorCode = errorCode;
    }
    
    public DCBackupRestoreException(final int errorCode, final Object errorInfo, final Throwable cause) {
        super(cause);
        this.errorCode = -1;
        this.errorInfo = null;
        this.errorMessage = null;
        this.errorDetail = null;
        this.errorCode = errorCode;
        this.errorInfo = errorInfo;
    }
    
    public int getErrorCode() {
        return this.errorCode;
    }
    
    public void setErrorInfo(final Object errorInfo) {
        this.errorInfo = errorInfo;
    }
    
    public Object getErrorInfo() {
        return this.errorInfo;
    }
    
    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public void setErrorDetail(final String errorDetail) {
        this.errorDetail = errorDetail;
    }
    
    public String getErrorMessage() {
        return this.errorMessage;
    }
    
    public String getErrorDetail() {
        return this.errorDetail;
    }
    
    @Override
    public String getMessage() {
        return this.errorMessage + " " + this.errorDetail;
    }
}
