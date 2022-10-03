package org.jscep.client;

public class ClientException extends Exception
{
    private static final long serialVersionUID = 1L;
    
    public ClientException(final Throwable cause) {
        super(cause);
    }
    
    public ClientException(final String message) {
        super(message);
    }
}
