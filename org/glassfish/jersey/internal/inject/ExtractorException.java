package org.glassfish.jersey.internal.inject;

import javax.ws.rs.ProcessingException;

public class ExtractorException extends ProcessingException
{
    private static final long serialVersionUID = -4918023257104413981L;
    
    public ExtractorException(final String message) {
        super(message);
    }
    
    public ExtractorException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public ExtractorException(final Throwable cause) {
        super(cause);
    }
}
