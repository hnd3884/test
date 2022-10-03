package com.me.mdm.chrome.agent.core.communication;

public class CommunicationStatus
{
    public static final int ERROR_UNKNOWN = -1;
    public static final int ERROR_NONE = 0;
    public static final int ERROR_UNKNOWN_HOST = 1;
    public static final int ERROR_NETWORK_UNREACHABLE = 2;
    public static final int ERROR_SOCKET_TIMEOUT = 3;
    public static final int ERROR_CONNECTION_REFUSED = 4;
    public static final int ERROR_DOWNLOAD_FAILED_DIRECTORY = 20;
    public static final int ERROR_DOWNLOAD_FAILED = 21;
    public static final int ERROR_DOWNLOAD_FAILED_FILE_NOT_FOUND = 22;
    public static final int ERROR_MALFORMED_URL = 23;
    public static final int ERROR_UPLOAD_UNKNOWN = 30;
    int statusCode;
    int errorCode;
    String errMessage;
    String dataBuffer;
    String downloadFilePath;
    
    public CommunicationStatus(final int statusCode, final String dataBuffer) {
        this.errorCode = 0;
        this.statusCode = statusCode;
        this.dataBuffer = dataBuffer;
    }
    
    public CommunicationStatus(final int statusCode) {
        this.errorCode = 0;
        this.statusCode = statusCode;
    }
    
    public CommunicationStatus(final int statusCode, final int errorCode) {
        this.errorCode = 0;
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.setErrorMessage(getMessageForError(errorCode));
    }
    
    public int getStatus() {
        return this.statusCode;
    }
    
    public String getErrorMessage() {
        return this.errMessage;
    }
    
    public void setStatus(final int statusCode) {
        this.statusCode = statusCode;
    }
    
    public void setErrorMessage(final String errorMessage) {
        if (errorMessage != null) {
            this.errMessage = errorMessage;
        }
    }
    
    public void setErrorCode(final int errorCode) {
        this.errorCode = errorCode;
        this.setErrorMessage(getMessageForError(errorCode));
    }
    
    public int getErrorCode() {
        return this.errorCode;
    }
    
    public String getUrlDataBuffer() {
        return this.dataBuffer;
    }
    
    public void setUrlDataBuffer(final String dataBuffer) {
        this.dataBuffer = dataBuffer;
    }
    
    public void setDownloadFilePath(final String filePath) {
        this.downloadFilePath = filePath;
    }
    
    public String getDownloadFilePath() {
        return this.downloadFilePath;
    }
    
    public static String getMessageForError(final int errorCode) {
        switch (errorCode) {
            case 1:
            case 2:
            case 3: {
                return "Server not reachable !";
            }
            default: {
                return "Error occurred. Try again later.";
            }
        }
    }
}
