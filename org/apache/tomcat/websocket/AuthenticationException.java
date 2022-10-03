package org.apache.tomcat.websocket;

public class AuthenticationException extends Exception
{
    private static final long serialVersionUID = 5709887412240096441L;
    
    public AuthenticationException(final String message) {
        super(message);
    }
}
