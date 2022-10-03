package javax.resource.spi;

import javax.resource.ResourceException;

public class SecurityException extends ResourceException
{
    public SecurityException(final String reason) {
        super(reason);
    }
    
    public SecurityException(final String reason, final String errorCode) {
        super(reason, errorCode);
    }
}
