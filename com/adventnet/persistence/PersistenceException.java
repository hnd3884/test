package com.adventnet.persistence;

public class PersistenceException extends Exception
{
    public PersistenceException(final String str, final Throwable cause) {
        super(str, cause);
    }
    
    public PersistenceException(final String str) {
        super(str);
    }
}
