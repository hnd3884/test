package com.adventnet.db.persistence.metadata;

import java.io.Serializable;

public class MetaDataException extends Exception implements Serializable
{
    private int errorCode;
    
    public int getErrorCode() {
        return this.errorCode;
    }
    
    public void setErrorCode(final int errorCode) {
        this.errorCode = errorCode;
    }
    
    public MetaDataException(final String message) {
        super(message);
        this.errorCode = -1;
    }
    
    public MetaDataException(final String message, final Throwable cause) {
        super(message, cause);
        this.errorCode = -1;
    }
}
