package org.jscep.transport.response;

import net.jcip.annotations.Immutable;

@Immutable
public class InvalidContentException extends ContentException
{
    private static final long serialVersionUID = 8144078591967730995L;
    
    public InvalidContentException(final Throwable cause) {
        super(cause);
    }
    
    public InvalidContentException(final String message) {
        super(message);
    }
}
