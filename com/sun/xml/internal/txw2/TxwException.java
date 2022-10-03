package com.sun.xml.internal.txw2;

public class TxwException extends RuntimeException
{
    private static final long serialVersionUID = 1L;
    
    public TxwException(final String message) {
        super(message);
    }
    
    public TxwException(final Throwable cause) {
        super(cause);
    }
    
    public TxwException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
