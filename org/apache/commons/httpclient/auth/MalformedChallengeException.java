package org.apache.commons.httpclient.auth;

import org.apache.commons.httpclient.ProtocolException;

public class MalformedChallengeException extends ProtocolException
{
    public MalformedChallengeException() {
    }
    
    public MalformedChallengeException(final String message) {
        super(message);
    }
    
    public MalformedChallengeException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
