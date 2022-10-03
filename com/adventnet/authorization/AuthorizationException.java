package com.adventnet.authorization;

public class AuthorizationException extends SecurityException
{
    public AuthorizationException(final String msg) {
        super(msg);
    }
    
    public AuthorizationException(final String msg, final Exception ex) {
        super(msg);
        this.initCause(new Throwable(msg, ex));
    }
}
