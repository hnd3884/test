package org.glassfish.hk2.api;

public class HK2Exception extends Exception
{
    private static final long serialVersionUID = -6933923442167686426L;
    
    public HK2Exception() {
    }
    
    public HK2Exception(final String message) {
        super(message);
    }
    
    public HK2Exception(final Throwable cause) {
        super(cause);
    }
    
    public HK2Exception(final String message, final Throwable cause) {
        super(message, cause);
    }
}
