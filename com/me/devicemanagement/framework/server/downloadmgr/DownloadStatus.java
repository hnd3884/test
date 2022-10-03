package com.me.devicemanagement.framework.server.downloadmgr;

public class DownloadStatus
{
    int dwnloadstatus;
    String errMessage;
    String dataBuffer;
    String lastModifiedTime;
    String errMsgKey;
    Object errMsgArgs;
    int sslValidationStatus;
    String sslValidationException;
    
    public int getSslValidationStatus() {
        return this.sslValidationStatus;
    }
    
    public void setSslValidationStatus(final int sslValidationStatus) {
        this.sslValidationStatus = sslValidationStatus;
    }
    
    public String getSslValidationException() {
        return this.sslValidationException;
    }
    
    public void setSslValidationException(final String sslValidationException) {
        this.sslValidationException = sslValidationException;
    }
    
    public DownloadStatus() {
    }
    
    public DownloadStatus(final int dwnstatus, final String errorMessage, final String errorMessageKey, final Object errorMessageArgs) {
        this.dwnloadstatus = dwnstatus;
        this.errMessage = errorMessage;
        this.errMsgKey = errorMessageKey;
        this.errMsgArgs = errorMessageArgs;
    }
    
    public DownloadStatus(final int dwnstatus, final String errorMessage) {
        this.dwnloadstatus = dwnstatus;
        this.errMessage = errorMessage;
        this.errMsgKey = errorMessage;
        this.errMsgArgs = "NO_I18N";
    }
    
    DownloadStatus(final int dwnstatus) {
        this.dwnloadstatus = dwnstatus;
    }
    
    public int getStatus() {
        return this.dwnloadstatus;
    }
    
    public String getErrorMessage() {
        return this.errMessage;
    }
    
    public String getErrorMessageKey() {
        return this.errMsgKey;
    }
    
    public Object getErrorMessageArgs() {
        return this.errMsgArgs;
    }
    
    public void setStatus(final int dwnstatus) {
        this.dwnloadstatus = dwnstatus;
    }
    
    public void setErrorMessage(final String errorMessage) {
        if (errorMessage != null) {
            this.errMessage = errorMessage;
        }
    }
    
    public String getUrlDataBuffer() {
        return this.dataBuffer;
    }
    
    public void setUrlDataBuffer(final String dataBuffer) {
        this.dataBuffer = dataBuffer;
    }
    
    public String getLastModifiedTime() {
        return this.lastModifiedTime;
    }
    
    public void setLastModifiedTime(final String lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }
    
    public void setErrMsgKey(final String errMsgKey) {
        this.errMsgKey = errMsgKey;
    }
    
    public void setErrMsgArgs(final Object errMsgArgs) {
        this.errMsgArgs = errMsgArgs;
    }
}
