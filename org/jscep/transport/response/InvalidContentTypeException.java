package org.jscep.transport.response;

import java.util.Arrays;
import net.jcip.annotations.Immutable;

@Immutable
public class InvalidContentTypeException extends ContentException
{
    private static final long serialVersionUID = 8144078591967730995L;
    
    public InvalidContentTypeException(final String actual, final String... expected) {
        this(String.format("Expected %s, but was %s", Arrays.toString(expected), actual));
    }
    
    public InvalidContentTypeException(final Throwable cause) {
        super(cause);
    }
    
    private InvalidContentTypeException(final String message) {
        super(message);
    }
}
