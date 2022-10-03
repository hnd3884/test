package com.adventnet.cli.transport;

public class LoginException extends Exception
{
    public LoginException() {
    }
    
    public LoginException(final String s) {
        super(s);
    }
    
    public LoginException(final String s, final Throwable t) {
        super(s, t);
    }
}
