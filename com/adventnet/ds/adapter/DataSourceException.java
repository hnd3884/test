package com.adventnet.ds.adapter;

public class DataSourceException extends Exception
{
    public DataSourceException() {
    }
    
    public DataSourceException(final String message) {
        super(message);
    }
    
    public DataSourceException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public DataSourceException(final Throwable cause) {
        super(cause);
    }
}
