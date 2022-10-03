package com.adventnet.nms.store;

public class NmsStorageException extends Exception
{
    private Exception ex;
    private String message;
    
    public NmsStorageException(final String message, final Exception ex) {
        this.ex = null;
        this.message = null;
        this.ex = ex;
        this.message = message;
    }
    
    public Exception getException() {
        return this.ex;
    }
    
    public String getMessage() {
        return this.message;
    }
}
