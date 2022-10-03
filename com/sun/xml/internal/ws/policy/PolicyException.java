package com.sun.xml.internal.ws.policy;

public class PolicyException extends Exception
{
    public PolicyException(final String message) {
        super(message);
    }
    
    public PolicyException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public PolicyException(final Throwable cause) {
        super(cause);
    }
}
