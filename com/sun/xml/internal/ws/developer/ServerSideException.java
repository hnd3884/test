package com.sun.xml.internal.ws.developer;

public class ServerSideException extends Exception
{
    private final String className;
    
    public ServerSideException(final String className, final String message) {
        super(message);
        this.className = className;
    }
    
    @Override
    public String getMessage() {
        return "Client received an exception from server: " + super.getMessage() + " Please see the server log to find more detail regarding exact cause of the failure.";
    }
    
    @Override
    public String toString() {
        final String s = this.className;
        final String message = this.getLocalizedMessage();
        return (message != null) ? (s + ": " + message) : s;
    }
}
