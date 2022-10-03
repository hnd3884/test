package com.adventnet.tools.prevalent;

public class ErrorDetails
{
    private int errorCode;
    private String errorCodeString;
    private String message;
    private String detailedMessage;
    
    public ErrorDetails() {
        this.errorCode = 0;
        this.errorCodeString = null;
        this.message = null;
        this.detailedMessage = null;
    }
    
    public ErrorDetails(final int errorCode, final String errorCodeString, final String message, final String detailedMessage) {
        this.errorCode = 0;
        this.errorCodeString = null;
        this.message = null;
        this.detailedMessage = null;
        this.errorCode = errorCode;
        this.errorCodeString = errorCodeString;
        this.message = message;
        this.detailedMessage = detailedMessage;
    }
    
    public void setErrorCode(final int errorCode) {
        this.errorCode = errorCode;
    }
    
    public void setErrorCode(final String errorCodeString) {
        this.errorCodeString = errorCodeString;
    }
    
    public void setErrorMessage(final String message) {
        this.message = message;
    }
    
    public void setDetailedMessage(final String detailedMessage) {
        this.detailedMessage = detailedMessage;
    }
    
    public int getErrorCode() {
        return this.errorCode;
    }
    
    public String getErrorCodeString() {
        return this.errorCodeString;
    }
    
    public String getErrorMessage() {
        return this.message;
    }
    
    public String getDetailedMessage() {
        return this.detailedMessage;
    }
}
