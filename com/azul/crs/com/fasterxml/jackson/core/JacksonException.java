package com.azul.crs.com.fasterxml.jackson.core;

import java.io.IOException;

public abstract class JacksonException extends IOException
{
    private static final long serialVersionUID = 123L;
    
    protected JacksonException(final String msg) {
        super(msg);
    }
    
    protected JacksonException(final Throwable t) {
        super(t);
    }
    
    protected JacksonException(final String msg, final Throwable rootCause) {
        super(msg, rootCause);
    }
    
    public abstract JsonLocation getLocation();
    
    public abstract String getOriginalMessage();
    
    public abstract Object getProcessor();
}
