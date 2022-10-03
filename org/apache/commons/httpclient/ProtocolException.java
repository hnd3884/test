package org.apache.commons.httpclient;

public class ProtocolException extends HttpException
{
    public ProtocolException() {
    }
    
    public ProtocolException(final String message) {
        super(message);
    }
    
    public ProtocolException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
