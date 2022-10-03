package org.apache.commons.httpclient.cookie;

import org.apache.commons.httpclient.ProtocolException;

public class MalformedCookieException extends ProtocolException
{
    public MalformedCookieException() {
    }
    
    public MalformedCookieException(final String message) {
        super(message);
    }
    
    public MalformedCookieException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
