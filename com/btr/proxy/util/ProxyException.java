package com.btr.proxy.util;

public class ProxyException extends Exception
{
    private static final long serialVersionUID = 1L;
    
    public ProxyException() {
    }
    
    public ProxyException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public ProxyException(final String message) {
        super(message);
    }
    
    public ProxyException(final Throwable cause) {
        super(cause);
    }
}
