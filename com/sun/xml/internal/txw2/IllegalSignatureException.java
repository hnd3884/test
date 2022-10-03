package com.sun.xml.internal.txw2;

public class IllegalSignatureException extends TxwException
{
    private static final long serialVersionUID = 1L;
    
    public IllegalSignatureException(final String message) {
        super(message);
    }
    
    public IllegalSignatureException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public IllegalSignatureException(final Throwable cause) {
        super(cause);
    }
}
