package org.jscep.transport.response;

import net.jcip.annotations.Immutable;
import org.jscep.transport.TransportException;

@Immutable
public class ContentException extends TransportException
{
    private static final long serialVersionUID = -959127316844320818L;
    
    public ContentException(final Throwable cause) {
        super(cause);
    }
    
    public ContentException(final String message) {
        super(message);
    }
}
