package org.glassfish.jersey.message.internal;

import javax.ws.rs.ProcessingException;

public class HeaderValueException extends ProcessingException
{
    private static final long serialVersionUID = 981810773601231157L;
    private final Context context;
    
    public HeaderValueException(final String message, final Throwable cause, final Context context) {
        super(message, cause);
        this.context = context;
    }
    
    public HeaderValueException(final String message, final Context context) {
        super(message);
        this.context = context;
    }
    
    public Context getContext() {
        return this.context;
    }
    
    public enum Context
    {
        INBOUND, 
        OUTBOUND;
    }
}
